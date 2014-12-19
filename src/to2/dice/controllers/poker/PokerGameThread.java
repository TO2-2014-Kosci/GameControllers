package to2.dice.controllers.poker;

import to2.dice.ai.Bot;
import to2.dice.controllers.GameController;
import to2.dice.controllers.common.GameThread;
import to2.dice.controllers.poker.hands.Hand;
import to2.dice.game.Dice;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;
import to2.dice.server.GameServer;

import java.util.*;

public class PokerGameThread extends GameThread {

    private final int rerollsNumber = 2;

    public int getRerollsNumber() {
        return rerollsNumber;
    }

    public PokerGameThread(GameServer server, GameController gameController, GameSettings settings, GameState state, Map<Player, Bot> bots) {
        super(server, gameController, settings, state, bots);
        setGameRoutine(new PokerGameRoutine());
    }

    private class PlayerHand implements Comparable<PlayerHand> {
        private final Player player;
        private final Hand hand;

        private PlayerHand(Player player, Hand hand) {
            this.player = player;
            this.hand = hand;
        }

        public Player getPlayer() {
            return player;
        }

        public Hand getHand() {
            return hand;
        }


        @Override
        public int compareTo(PlayerHand playerHand) {
            return this.hand.compareTo(playerHand.getHand());
        }
    }

    private class PokerPlayerComparator implements Comparator<Map.Entry<Player, Hand>> {

        @Override
        public int compare(Map.Entry<Player, Hand> entry1, Map.Entry<Player, Hand> entry2) {
            return entry1.getValue().compareTo(entry2.getValue());
        }
    }

    private class PokerGameRoutine implements Runnable {
        @Override
        public void run() {
            try {
                synchronized (gameController) {
                    state.setGameStarted(true);

                    while (state.getCurrentRound() < settings.getRoundsToWin()) {
                        startNewRound();
                        rollInitialDice();

                        for (int rerollNumber = 1; rerollNumber <= rerollsNumber; rerollNumber++) {

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

                        Player roundWinner = getRoundWinner();
                        addPointToPlayer(roundWinner);
                    }

                    state.setGameStarted(false);
                    server.sendToAll(gameController, state);
                }
            } catch (InterruptedException e) {
                System.out.println("Fatal Error: GameThread interrupted!");
            }
        }

    }

    private Player getRoundWinner() {
        List<PlayerHand> playerHand = new ArrayList<PlayerHand>();
        for (Player player : state.getPlayers()) {
            playerHand.add(new PlayerHand(player, HandFactory.createHandFromDice(player.getDice())));
        }
        return Collections.max(playerHand).getPlayer();
    }


}
