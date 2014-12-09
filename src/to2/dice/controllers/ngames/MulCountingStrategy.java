package to2.dice.controllers.ngames;

import to2.dice.game.Dice;

public class MulCountingStrategy implements CountingStrategy {
    @Override
    public int countPoints(Dice dice) {
        int result = 0;
        for(int i: dice.getDice()){
            result *= i;
        }
        return result;
    }
}