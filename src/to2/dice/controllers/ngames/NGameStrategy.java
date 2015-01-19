package to2.dice.controllers.ngames;

import to2.dice.controllers.GameStrategy;
import to2.dice.game.*;

import java.util.Random;

import static java.lang.Thread.sleep;

public class NGameStrategy extends GameStrategy {
    private CountingStrategy countingStrategy;
    private Player currentPlayer;
    private int afterRerollWaitTime = 5000;

    public NGameStrategy(GameSettings settings, NGameState state, CountingStrategy countingStrategy) {
        super(settings, state);
        this.countingStrategy = countingStrategy;
    }

    public void setAfterRerollWaitTime(int time){ afterRerollWaitTime = time; }

    @Override
    protected void startNewRound() {
        super.startNewRound();
        ((NGameState) state).setWinningNumber(
                countingStrategy.generateWinningNumber(settings.getDiceNumber())
        );
        checkStartingHand();
        chooseRandomPlayer();
    }

    @Override
    protected void nextPlayer(){
        if(isWinner(currentPlayer)) {
            addPointToPlayer(currentPlayer);
            state.setCurrentPlayer(null);
            roomController.updateGameState();
            try {
                sleep(afterRerollWaitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (state.getCurrentRound() < settings.getRoundsToWin()) {
                startNewRound();
            } else {
                finishGame();
            }
        }
        else if (state.isGameStarted()) {
            if (!currentPlayerIt.hasNext()) {
                currentPlayerIt = state.getPlayers().listIterator();
            }
            currentPlayer = currentPlayerIt.next();
            state.setCurrentPlayer(currentPlayer);
            moveTimer.start();
        }
    }

    private void checkStartingHand(){
        for (Player player : state.getPlayers()) {
            while(isWinner(player)) {
                player.setDice(diceRoller.rollDice());
            }
        }
    }

    private boolean isWinner(Player p){
        return ((NGameState)state).getWinningNumber() == countingStrategy.countPoints(p.getDice());
    }

    private void chooseRandomPlayer(){
        Random rand = new Random();
        int index = rand.nextInt(settings.getMaxPlayers());

        currentPlayer = state.getPlayers().get(index);
        currentPlayerIt = state.getPlayers().listIterator(index+1);
        state.setCurrentPlayer(currentPlayer);
    }
}
