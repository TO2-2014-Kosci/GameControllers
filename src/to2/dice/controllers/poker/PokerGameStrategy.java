package to2.dice.controllers.poker;

import to2.dice.controllers.GameStrategy;
import to2.dice.controllers.MoveTimer;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;

import java.util.*;

import static java.lang.Thread.sleep;

public class PokerGameStrategy extends GameStrategy {

    private final int lastRerollWaitTime = 5;

    private final int rerollsInRound = 2;
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
            if (currentRerollNumber < rerollsInRound) {
                /* it was not last reroll in this round, start new reroll */
                currentRerollNumber++;
                currentPlayerIt = state.getPlayers().listIterator();
            } else {
                /* it was last reroll in this round */
                Player roundWinner = getRoundWinner();
                addPointToPlayer(roundWinner);

                state.setCurrentPlayer(null);
                roomController.updateGameState();
                try {
                    sleep(lastRerollWaitTime *1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


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
            moveTimer.start();
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
