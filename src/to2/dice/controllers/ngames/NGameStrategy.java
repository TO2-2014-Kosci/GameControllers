package to2.dice.controllers.ngames;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import to2.dice.controllers.GameStrategy;
import to2.dice.game.*;

public class NGameStrategy extends GameStrategy {
    private CountingStrategy countingStrategy;
    private boolean winningHand;

    public NGameStrategy(GameSettings settings, NGameState state, CountingStrategy countingStrategy) {
        super(settings, state);
        this.countingStrategy = countingStrategy;
    }

    @Override
    public void startGame() {
        throw new NotImplementedException();
    }

    @Override
    public void reroll(boolean[] chosenDice) {
        throw new NotImplementedException();
    }

    /*
    private class NGameRoutine implements Runnable{
        @Override
        public void run() {
            try {
                synchronized (roomController) {
                    state.setGameStarted(true);

                    while(state.getCurrentRound() < settings.getRoundsToWin()){
                        startNewRound();
                        winningHand = false;
                        rollInitialDice();

                        while(!winningHand){
                            for (Player currentPlayer : state.getPlayers()) {
                                state.setCurrentPlayer(currentPlayer);
                                roomController.updateGameState();

                                if (currentPlayer.isBot()) {
                                    chosenDice = playerBotMap.get(currentPlayer).makeMove(currentPlayer.getDice().getDiceArray(),
                                            getOtherDiceArrays(currentPlayer));
                                } else {
                                    while (chosenDice == null) {
                                        roomController.wait();
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

                                if(countingStrategy.countPoints(currentPlayerDice) == ((NGameState)state).getWinningNumber()){
                                    winningHand = true;
                                    addPointToPlayer(currentPlayer);
                                }
                            }
                        }
                    }

                    state.setGameStarted(false);
                    roomController.updateGameState();
                }
            }
            catch(InterruptedException e) {
                System.out.println("Fatal Error: GameThread interrupted!");
            }
        }
    }*/
}
