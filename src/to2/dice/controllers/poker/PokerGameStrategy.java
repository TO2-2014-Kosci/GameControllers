package to2.dice.controllers.poker;

import to2.dice.controllers.GameStrategy;
import to2.dice.game.Dice;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;

import java.util.*;

public class PokerGameStrategy extends GameStrategy {

    private final int rerollsNumber = 2;
    private int currentRerollNumber;

    public PokerGameStrategy(GameSettings settings, GameState state) {
        super(settings, state);
    }

    @Override
    protected void startNewRound() {
        super.startNewRound();
        currentRerollNumber = 1;
    }

    @Override
    protected void nextPlayer() {
        if (!currentPlayerIt.hasNext()) {
            if (currentRerollNumber < rerollsNumber) {
                /* it was not last reroll in this round, start new reroll */
                currentRerollNumber++;
                currentPlayerIt = state.getPlayers().listIterator();
            } else {
                /* it was last reroll in this round */
                Player roundWinner = getRoundWinner();
                addPointToPlayer(roundWinner);

                if (state.getCurrentRound() < settings.getRoundsToWin()) {
                    /* it was not last round */
                    startNewRound();
                    currentPlayerIt = state.getPlayers().listIterator();
                } else {
                    /* it was last round */
                    finishGame();
                }
            }
        }
        if (state.isGameStarted()) {
            state.setCurrentPlayer(currentPlayerIt.next());
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
