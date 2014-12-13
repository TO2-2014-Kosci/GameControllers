package to2.dice.controllers.ngames;

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