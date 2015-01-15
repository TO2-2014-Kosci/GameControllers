package to2.dice.controllers.ngames;

import to2.dice.controllers.GameStrategy;
import to2.dice.game.*;

import java.util.Random;

public class NGameStrategy extends GameStrategy {
    private CountingStrategy countingStrategy;
    private Player currentPlayer;

    public NGameStrategy(GameSettings settings, NGameState state, CountingStrategy countingStrategy) {
        super(settings, state);
        this.countingStrategy = countingStrategy;
    }

    @Override
    protected void startNewRound() {
        super.startNewRound();
        //TODO: initial hand is not winning Hand
        generateRandomPlayer();

        ((NGameState) state).setWinningNumber(
                countingStrategy.generateWinningNumber(settings.getDiceNumber())
        );
    }

    @Override
    protected void nextPlayer(){
        if(isWinner(currentPlayer)) {
            addPointToPlayer(currentPlayer);
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
        }
    }

    private boolean isWinner(Player p){
        return ((NGameState)state).getWinningNumber() == countingStrategy.countPoints(p.getDice());
    }

    private void generateRandomPlayer(){
        Random rand = new Random();
        int index = rand.nextInt(settings.getMaxPlayers());

        currentPlayer = state.getPlayers().get(index);
        currentPlayerIt = state.getPlayers().listIterator(index+1);
        state.setCurrentPlayer(currentPlayer);
    }
}
