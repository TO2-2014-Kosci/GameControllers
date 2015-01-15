package to2.dice.controllers;

import to2.dice.game.Dice;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class DiceRoller {

    private SecureRandom random;
    private int diceNumber;

    public DiceRoller(int diceNumber) {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.diceNumber = diceNumber;
    }

    public int rollSingleDice() {
        return (random.nextInt(6) + 1);
    }

    public Dice rollDice() {
        Dice dice = new Dice(this.diceNumber);

        int[] diceArray = new int[this.diceNumber];
        for (int i = 0; i < this.diceNumber; i++) {
            diceArray[i] = rollSingleDice();
        }

        dice.setDiceArray(diceArray);
        return dice;
    }
}