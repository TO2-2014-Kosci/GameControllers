package to2.dice.controllers.ngames;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import to2.dice.controllers.GameStrategy;
import to2.dice.game.*;

public class NGameStrategy extends GameStrategy {
    private CountingStrategy countingStrategy;
    private Player currentPlayer = state.getPlayers().get(0);

    public NGameStrategy(GameSettings settings, NGameState state, CountingStrategy countingStrategy) {
        super(settings, state);
        this.countingStrategy = countingStrategy;
    }

    @Override
    protected void startNewRound() {
        super.startNewRound();
        //TODO: initial hand is not winning Hand and maybe random player starting round
        ((NGameState)state).setWinningNumber(
                diceRoller.rollNGamePoints(countingStrategy.countMax(settings.getDiceNumber()))
        );
    }

    @Override
    protected void nextPlayer(){
        if (!currentPlayerIt.hasNext()) {
            currentPlayerIt = state.getPlayers().listIterator();
        }

        if(isWinner(currentPlayer)) {
            state.setCurrentRound(state.getCurrentRound() + 1);
            if (state.getCurrentRound() < settings.getRoundsToWin()) startNewRound();
            else finishGame();
        }

        if (state.isGameStarted()) {
            state.setCurrentPlayer(currentPlayerIt.next());
        }
    }

    private boolean isWinner(Player p){
        return ((NGameState)state).getWinningNumber() == countingStrategy.countPoints(p.getDice());
    }
}
