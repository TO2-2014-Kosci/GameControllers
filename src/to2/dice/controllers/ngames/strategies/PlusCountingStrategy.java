package to2.dice.controllers.ngames.strategies;

import to2.dice.controllers.ngames.CountingStrategy;
import to2.dice.game.Dice;

public class PlusCountingStrategy extends CountingStrategy {
    @Override
    public int countPoints(Dice dice) {
        int result = 0;
        for (int i : dice.getDiceArray()) {
            result += i;
        }
        return result;
    }

    @Override
    public int generateWinningNumber(int diceNumber) {
        int sumToThrow = 0;
        for(int i = 0; i < diceNumber; i++ ){
            sumToThrow += random.nextInt(6) + 1;
        }
        return sumToThrow;
    }
}