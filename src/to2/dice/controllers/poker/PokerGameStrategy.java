package to2.dice.controllers.poker;

import to2.dice.controllers.GameStrategy;
import to2.dice.game.Dice;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;

import java.util.*;

public class PokerGameStrategy extends GameStrategy {

    private ListIterator<Player> currentPlayerIt;
    private final int rerollsNumber = 2;
    private int currentRerollNumber;

    public PokerGameStrategy(GameSettings settings, GameState state) {
        super(settings, state);
    }

    @Override
    public void startGame() {
        currentPlayerIt = state.getPlayers().listIterator();
        state.setGameStarted(true);
        startNewRound();
        nextPlayer();
    }

    @Override
    public void reroll(boolean[] chosenDice) {
        Dice currentPlayerDice = state.getCurrentPlayer().getDice();
        int[] diceArray = currentPlayerDice.getDiceArray();

        for (int i = 0; i < settings.getDiceNumber(); i++) {
            if (chosenDice[i]) {
                diceArray[i] = diceRoller.rollSingleDice();
            }
        }
        currentPlayerDice.setDiceArray(diceArray);
        state.getCurrentPlayer().setDice(currentPlayerDice);

        nextPlayer();
    }

    private void startNewRound() {
        state.setCurrentRound(state.getCurrentRound() + 1);
        currentRerollNumber = 1;
        rollInitialDice();
    }

    private void rollInitialDice() {
        for (Player player : state.getPlayers()) {
            player.setDice(diceRoller.rollDice());
        }
    }

    private void nextPlayer() {
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
                    /* it was not last round*/
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

    private void finishGame() {
        state.setGameStarted(false);
    }

}
