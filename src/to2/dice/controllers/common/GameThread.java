package to2.dice.controllers.common;

import to2.dice.ai.Bot;
import to2.dice.controllers.AbstractGameController;
import to2.dice.controllers.GameController;
import to2.dice.game.Dice;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;
import to2.dice.server.GameServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameThread {

    private final int REROLLS_NUMBER = 2;
    private final GameController gameController;
    private final GameServer server;
    private final GameSettings settings;
    private final GameState state;
    private final DiceRoller diceRoller;
    private RoomController roomController;
    private Map<Player, Bot> bots;
    private Map<Player, Integer> numberOfAbsences = new HashMap<Player, Integer>();
    private boolean[] chosenDice;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public GameThread(GameServer server, GameController gameController, GameSettings settings, GameState state, Map<Player, Bot> bots) {
        this.gameController = gameController;
        this.server = server;
        this.settings = settings;
        this.state = state;
        this.bots = bots;
        diceRoller = new DiceRoller(settings.getDiceNumber());
    }

    public void setRoomController(RoomController roomController) {
        this.roomController = roomController;
    }

    public void start() {
        executor.execute(new GameRoutine());
    }

    public void interrupt() {
        executor.shutdownNow();
    }

    public int getRerollsNumber() {
        return REROLLS_NUMBER;
    }

    public void handleRerollRequest(boolean[] chosenDice) {
        synchronized (gameController) {
            this.chosenDice = chosenDice;
            gameController.notify();
        }
    }

    public String getCurrentPlayerName() {
        synchronized (gameController) {
            return state.getCurrentPlayer().getName();
        }
    }


    private class GameRoutine implements Runnable {

        @Override
        public void run() {
            try {
                synchronized (gameController) {
                    state.setGameStarted(true);


                    while (state.getCurrentRound() < settings.getRoundsToWin()) {
                        startNewRound();
                        rollInitialDice();

                        for (int rerollNumber = 1; rerollNumber <= REROLLS_NUMBER; rerollNumber++) {

                            for (Player currentPlayer : state.getPlayers()) {
                                state.setCurrentPlayer(currentPlayer);
                                server.sendToAll(gameController, state);

                                if (currentPlayer.isBot()) {
                                    chosenDice = bots.get(currentPlayer).makeMove(currentPlayer.getDice().getDiceArray(),
                                            getOtherDiceArrays(currentPlayer));
                                } else {
                                    while (chosenDice == null) {
                                        gameController.wait();
                                    }
                                }

                                Dice currentPlayerDice = currentPlayer.getDice();
                                int[] diceArray = currentPlayerDice.getDiceArray();

                                for (int i = 0; i < settings.getDiceNumber(); i++) {
                                    if (chosenDice[i]) {
                                        diceArray[i] = diceRoller.rollSingleDice();
                                    }
                                }
                                chosenDice = null;
                                currentPlayerDice.setDiceArray(diceArray);
                                state.getCurrentPlayer().setDice(currentPlayerDice);
                            }
                        }

//                        Player roundWinner = getRoundWinner();
//                        addPointToPlayer(roundWinner);
                    }

                    state.setGameStarted(false);
                    server.sendToAll(gameController, state);
                }
            } catch (InterruptedException e) {
                System.out.println("Fatal Error: GameThread interrupted!");
            }
        }

        private void startNewRound() {
            state.setCurrentRound(state.getCurrentRound() + 1);
        }

        private void rollInitialDice() {
            for (Player player : state.getPlayers()) {
                player.setDice(diceRoller.rollDice());
            }
        }

        private List<int[]> getOtherDiceArrays(Player player) {
            List<int[]> otherDice = new ArrayList<>();
            for (Player p : state.getPlayers()) {
                if (p != player) {
                    otherDice.add(p.getDice().getDiceArray());
                }
            }
            return otherDice;
        }

        private void addPointToPlayer(Player player) {
            player.setScore(player.getScore() + 1);
        }

        //TODO WRONG! winner PokerHands checking
//        private Player getRoundWinner() {
//            return gameController.getWinner(state.getPlayers());
//        }

        private void addPenaltyToPlayer(Player player) {
            int currentAbsences = numberOfAbsences.get(player);
            currentAbsences++;
            if (currentAbsences == settings.getMaxInactiveTurns())
                removePlayer(player.getName());
            else
                numberOfAbsences.put(player, currentAbsences);
        }

        public void removePlayer(String senderName) {

        }
    }
}