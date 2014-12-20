package to2.dice.controllers.ngames;

import to2.dice.ai.Bot;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameThread;
import to2.dice.game.*;
import to2.dice.server.GameServer;

import java.util.Map;

public class NGameThread extends GameThread {
    private CountingStrategy strategy;
    private boolean winningHand;

    public NGameThread(GameServer server, GameController gameController, GameSettings settings,
                       NGameState state, Map<Player, Bot> bots, CountingStrategy strategy) {

        super(server, gameController, settings, state, bots);
        this.strategy = strategy;
        setGameRoutine(new NGameRoutine());
    }

    private class NGameRoutine implements Runnable{
        @Override
        public void run() {
            try {
                synchronized (gameController) {
                    state.setGameStarted(true);

                    while(state.getCurrentRound() < settings.getRoundsToWin()){
                        startNewRound();
                        winningHand = false;
                        rollInitialDice();

                        while(!winningHand){
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

                                if(strategy.countPoints(currentPlayerDice) == ((NGameState)state).getWinningNumber()){
                                    winningHand = true;
                                    addPointToPlayer(currentPlayer);
                                }
                            }
                        }
                    }

                    state.setGameStarted(false);
                    server.sendToAll(gameController, state);
                }
            }
            catch(InterruptedException e) {
                System.out.println("Fatal Error: GameThread interrupted!");
            }
        }
    }
}
