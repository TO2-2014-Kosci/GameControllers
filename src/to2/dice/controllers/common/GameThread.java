package to2.dice.controllers.common;

import to2.dice.ai.Bot;
import to2.dice.controllers.AbstractGameController;
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

    private AbstractGameController abstractGameController;
    private GameServer server;
    private GameSettings settings;
    private GameState state;
    private Generator generator;
    private Map<Player, Bot> bots;
    private Map<Player, Integer> numberOfAbsences = new HashMap<Player, Integer>();
    private final int REROLLS_NUMBER = 2;
    private boolean[] chosenDice;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private class GameRoutine implements Runnable {
        @Override
        public void run() {
            try {
                synchronized (abstractGameController) {
                    state.setGameStarted(true);

                    while (state.getCurrentRound() < settings.getRoundsToWin()) {
                        startNewRound();

                        for (int rerollNumber = 1; rerollNumber <= REROLLS_NUMBER; rerollNumber++) {

                            for (Player currentPlayer : state.getPlayers()) {
                                state.setCurrentPlayer(currentPlayer);
                                server.sendToAll(abstractGameController, state);

                                if (currentPlayer.isBot()) {
                                    chosenDice = bots.get(currentPlayer).makeMove(currentPlayer.getDice().getDice(),
                                            getOtherDice(currentPlayer));
                                }
                                else {
                                    while (chosenDice == null) {
                                        abstractGameController.wait();
                                    }
                                }

                                Dice currentPlayerDice = currentPlayer.getDice();
                                int[] dice_tab = currentPlayerDice.getDice();

                                for (int i = 0; i < settings.getDiceNumber(); i++) {
                                    if (chosenDice[i]) {
                                        dice_tab[i] = generator.diceThrow();
                                    }
                                }
                                chosenDice = null;
                                currentPlayerDice.setDice(dice_tab);
                                state.getCurrentPlayer().setDice(currentPlayerDice);
                            }

                            Player winner = getWinner();
                            addPointToPlayer(winner);
                        }
                    }
                    state.setGameStarted(false);
                    server.sendToAll(abstractGameController, state);
                }
            } catch (InterruptedException e) {
                System.out.println("Fatal Error: GameThread interrupted!");
            }
        }

        //TODO WRONG! winner PokerHands checking
        private Player getWinner() {
            return abstractGameController.getWinner(state.getPlayers());
        }
    }

    private List<Dice> getOtherDice(Player player) {
        List<Dice> otherDice = new ArrayList<Dice>();
        for (Player p : state.getPlayers()) {
            if (p != player) {
                otherDice.add(p.getDice());
            }
        }
        return otherDice;
    }

    public GameThread(GameServer server, AbstractGameController abstractGameController, GameSettings settings, GameState state, Map<Player, Bot> bots) {
        this.abstractGameController = abstractGameController;
        this.server = server;
        this.settings = settings;
        this.state = state;
        this.bots = bots;
        generator = new Generator(settings.getDiceNumber());
    }


    public void start() {
        executor.execute(new GameRoutine());
    }

    public synchronized void handleRerollRequest(boolean[] chosenDice) {

//        while ()
        this.chosenDice = chosenDice;
        abstractGameController.notify();
    }

    public void removePlayer(String senderName) {

    }

    private void startNewRound() {
        state.setCurrentRound(state.getCurrentRound() + 1);

        for (Player player : state.getPlayers()) {
            player.setDice(generator.rollDice());
        }
    }

    private void addPenaltyToPlayer(Player player) {
        int currentAbsences = numberOfAbsences.get(player);
        currentAbsences++;
        if (currentAbsences == settings.getMaxInactiveTurns())
            removePlayer(player.getName());
        else
            numberOfAbsences.put(player, currentAbsences);
    }

    private void addPointToPlayer(Player player) {
        player.setScore(player.getScore() + 1);
    }
}