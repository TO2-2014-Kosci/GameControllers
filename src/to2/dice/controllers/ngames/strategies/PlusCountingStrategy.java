package to2.dice.controllers.ngames.strategies;

import to2.dice.controllers.ngames.CountingStrategy;
import to2.dice.game.Dice;

public class PlusCountingStrategy implements CountingStrategy {
    @Override
    public int countPoints(Dice dice) {
        int result = 0;
        for (int i : dice.getDiceArray()) {
            result += i;
        }
        return result;
    }
}