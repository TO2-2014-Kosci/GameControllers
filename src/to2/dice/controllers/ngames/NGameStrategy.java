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
        //TODO: initial hand is not winning Hand and maybe random player starting round
        generateRandomPlayer();

        ((NGameState) state).setWinningNumber(
                countingStrategy.generateWinningNumber(settings.getDiceNumber())
        );
    }

    @Override
    protected void nextPlayer(){
        if (!currentPlayerIt.hasNext()) {
            currentPlayerIt = state.getPlayers().listIterator();
        }

        if(isWinner(currentPlayer)) {
            if (state.getCurrentRound() < settings.getRoundsToWin()) {
                startNewRound();
            } else {
                finishGame();
            }
        }

        if (state.isGameStarted()) {
            state.setCurrentPlayer(currentPlayerIt.next());
        }
    }

    private boolean isWinner(Player p){
        return ((NGameState)state).getWinningNumber() == countingStrategy.countPoints(p.getDice());
    }

    private void generateRandomPlayer(){
        Random rand = new Random();
        for(int i = rand.nextInt(settings.getMaxPlayers()); i>0; i--)
            currentPlayer = currentPlayerIt.next();
        if(!currentPlayerIt.hasNext()) currentPlayerIt = state.getPlayers().listIterator();

        currentPlayer = currentPlayerIt.next();
    }
}
