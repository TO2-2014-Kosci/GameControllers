package to2.dice.controllers.poker;

import to2.dice.ai.Bot;
import to2.dice.controllers.GameController;
import to2.dice.controllers.common.GameThread;
import to2.dice.game.Dice;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;
import to2.dice.server.GameServer;

import java.util.Map;

public class PokerGameThread extends GameThread {

    private class PokerGameRoutine implements Runnable {

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
    }

    public PokerGameThread(GameServer server, GameController gameController, GameSettings settings, GameState state, Map<Player, Bot> bots) {
        super(server, gameController, settings, state, bots);
        setGameRoutine(new PokerGameRoutine());
    }


}
