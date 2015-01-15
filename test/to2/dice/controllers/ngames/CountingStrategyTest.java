package to2.dice.controllers.ngames;

import org.junit.Test;
import to2.dice.controllers.ngames.strategies.MulCountingStrategy;
import to2.dice.controllers.ngames.strategies.PlusCountingStrategy;
import to2.dice.game.Dice;
import to2.dice.game.Player;

import static org.junit.Assert.*;

public class CountingStrategyTest {
    int diceNum = 5;
    CountingStrategy csPlus = new PlusCountingStrategy();
    CountingStrategy csMul = new MulCountingStrategy();

    @Test
    public void testCountPoints() throws Exception {
        System.out.println("TESTING STRATEGIES COUNTING ALGORITHM");

        Player p1 = new Player("p1", false, diceNum);
        Player p2 = new Player("p2", false, diceNum);
        Player p3 = new Player("p3", false, diceNum);

        /* Setting dice for players */
        Dice d1 = new Dice(diceNum);
        int[] dArray1 = new int[5];
        java.util.Arrays.fill(dArray1, 1);
        d1.setDiceArray(dArray1);
        p1.setDice(d1);

        Dice d2 = new Dice(diceNum);
        int[] dArray2 = new int[5];
        java.util.Arrays.fill(dArray2, 2);
        d2.setDiceArray(dArray2);
        p2.setDice(d2);

        Dice d3 = new Dice(diceNum);
        int[] dArray3 = new int[5];
        java.util.Arrays.fill(dArray3, 3);
        d3.setDiceArray(dArray3);
        p3.setDice(d3);
        dArray3[0] = 2;
        dArray3[4] = 5;

        assertTrue("For plusCountingStrategy for first player the result should be diceNum * 1",
                csPlus.countPoints(p1.getDice()) == diceNum);
        assertTrue("For plusCountingStrategy for second player the result should be diceNum * 2",
                csPlus.countPoints(p2.getDice()) == diceNum * 2);
        assertTrue("For plusCountingStrategy for second player the result should be diceNum * 3 + 1",
                csPlus.countPoints(p3.getDice()) == diceNum * 3 + 1);

        assertTrue("For mulCountingStrategy for first player the result should be 1",
                csMul.countPoints(p1.getDice()) == 1);
        assertTrue("For mulCountingStrategy for second player the result should be 2^diceNum",
                csMul.countPoints(p2.getDice()) == java.lang.Math.pow(2, diceNum));
        assertTrue("For mulCountingStrategy for third player the result should be 3^(diceNum-2) * 2 * 5",
                csMul.countPoints(p3.getDice()) == java.lang.Math.pow(3, diceNum - 2) * 2 * 5);
    }

    @Test
    public void testGenerateWinningNumber() throws Exception {
        System.out.println("TESTING STRATEGIES GENERATOR OF WINNING NUMBER");

        boolean isInRange = true;
        int generated;
        for(int i = 0; i < 5; i++){
            generated = csPlus.generateWinningNumber(diceNum);
            if(generated < diceNum || generated > 6 * diceNum){
                isInRange = false;
                break;
            }
        }
        assertTrue("One of the generated number in plusStrategy wasn't in range", isInRange);

        isInRange = true;
        for(int i = 0; i < 5; i++){
            generated = csMul.generateWinningNumber(diceNum);
            if(generated < 1 || generated > java.lang.Math.pow(6, diceNum)){
                isInRange = false;
                break;
            }
        }
        assertTrue("One of the generated number in mulStrategy wasn't in range", isInRange);
    }
}