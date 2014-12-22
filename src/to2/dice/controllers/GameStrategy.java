package to2.dice.controllers;

import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GameStrategy {
    protected final GameSettings settings;
    protected final GameState state;
    protected DiceRoller diceRoller;
    //private Map<Player, Integer> numberOfAbsences = new HashMap<>();

    public GameStrategy(GameSettings settings, GameState state) {
        this.settings = settings;
        this.state = state;
        this.diceRoller = new DiceRoller(settings.getDiceNumber());
    }

    public abstract void startGame();

    public abstract void reroll(boolean[] chosenDice);

    public void addPointToPlayer(Player player) {
        player.setScore(player.getScore() + 1);
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
