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
        boolean delayed = false;
        if (!currentPlayerIt.hasNext()) {
            if (currentRerollNumber < rerollsInRound) {
                /* it was not last reroll in this round, start new reroll */
                currentRerollNumber++;
                currentPlayerIt = state.getPlayers().listIterator();
            } else {
                /* it was last reroll in this round */
                delayed = true;

                Player roundWinner = getRoundWinner();
                addPointToPlayer(roundWinner);

                state.setCurrentPlayer(null);

                gameStateTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (isLastRoundConditionMet()) {
                    /* it was last round */
                            finishGame();
                        } else {
                    /* it was not last round */
                            startNewRound();
                            currentPlayerIt = state.getPlayers().listIterator();
                            setNextPlayer();
                        }
                        roomController.updateGameState();
                    }
                }, lastRerollWaitTime*1000);
            }
        }
        if (state.isGameStarted() && !delayed) {
            setNextPlayer();
        }
    }

    private void setNextPlayer() {
        state.setCurrentPlayer(currentPlayerIt.next());
        moveTimer.start();
    }

    private Player getRoundWinner() {
        List<PlayerHand> playerHand = new ArrayList<PlayerHand>();
        for (Player player : state.getPlayers()) {
            playerHand.add(new PlayerHand(player, HandFactory.createHandFromDice(player.getDice())));
        }
        return Collections.max(playerHand).getPlayer();
    }
}
