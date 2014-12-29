package to2.dice.controllers;

import to2.dice.game.Dice;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;

import java.util.*;

public abstract class GameStrategy {
    protected ListIterator<Player> currentPlayerIt;
    protected final GameSettings settings;
    protected final GameState state;
    protected DiceRoller diceRoller;
    //private Map<Player, Integer> numberOfAbsences = new HashMap<>();

    public GameStrategy(GameSettings settings, GameState state) {
        this.settings = settings;
        this.state = state;
        this.diceRoller = new DiceRoller(settings.getDiceNumber());
    }

    public void startGame(){
        currentPlayerIt = state.getPlayers().listIterator();
        state.setGameStarted(true);
        startNewRound();
        nextPlayer();
    }

    protected void startNewRound() {
        state.setCurrentRound(state.getCurrentRound() + 1);
        rollInitialDice();
    }

    private void rollInitialDice() {
        for (Player player : state.getPlayers()) {
            player.setDice(diceRoller.rollDice());
        }
    }

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

    protected abstract void nextPlayer();

    public void addPointToPlayer(Player player) {
        player.setScore(player.getScore() + 1);
    }

    protected void finishGame() {
        state.setGameStarted(false);
    }

    //TODO uncomment and fix with implementation of MoveTimer
    /*public void addPenaltyToPlayer(Player player) {
        if (!numberOfAbsences.containsKey(player)) {
            numberOfAbsences.put(player, 0);
        }
        int currentAbsences = numberOfAbsences.get(player);
        currentAbsences++;
        if (currentAbsences == settings.getMaxInactiveTurns()) {
            removePlayer(player.getName());
        } else {
            numberOfAbsences.put(player, currentAbsences);
        }
    }

    protected void removePlayer(String senderName) {
    }*/
}
