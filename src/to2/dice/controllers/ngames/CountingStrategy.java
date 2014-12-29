package to2.dice.controllers.ngames;

import to2.dice.game.Dice;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public abstract class CountingStrategy {

    protected SecureRandom random;

    public CountingStrategy() {
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public abstract int countPoints(Dice dice);

    public abstract int generateWinningNumber(int diceNumber);
}