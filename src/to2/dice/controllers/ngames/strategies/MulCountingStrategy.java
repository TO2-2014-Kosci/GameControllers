package to2.dice.controllers.ngames.strategies;

import to2.dice.controllers.ngames.CountingStrategy;
import to2.dice.game.Dice;

public class MulCountingStrategy implements CountingStrategy {
    @Override
    public int countPoints(Dice dice) {
        int result = 1;
        for (int i : dice.getDiceArray()) {
            result *= i;
        }
        return result;
    }

    @Override
    public int countMax(int diceNum){
        int[] diceArr = new int[diceNum];
        java.util.Arrays.fill(diceArr, 6);

        Dice dice = new Dice(diceArr);
        return countPoints(dice);
    }
}