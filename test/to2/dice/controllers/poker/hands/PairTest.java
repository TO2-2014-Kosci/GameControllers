package to2.dice.controllers.poker.hands;


import org.junit.Test;

import static org.junit.Assert.*;

public class PairTest {

    @Test
    public void testCompareTo() throws Exception {
        Pair pair = new Pair(5, new int[]{4,2,1});

        Pair greaterPair = new Pair(6, new int[]{3,4,5});
        Pair lowerPair = new Pair(2, new int[]{6,2,1});

        Pair greaterPairEq = new Pair(3, new int[]{3, 1, 2});
        Pair lowerPairEq = new Pair(3, new int[]{3, 1, 1});


        assertTrue("Greater pair is not taken as greater", pair.compareTo(greaterPair) < 0);
        assertTrue("Lower pair is not taken as lower", greaterPair.compareTo(pair) > 0);

        assertTrue("Greater pair is not taken as greater", pair.compareTo(lowerPair) > 0);
        assertTrue("Lower pair is not taken as lower", lowerPair.compareTo(pair) < 0);

        assertTrue("Greater pair with second element is not taken as greater", greaterPairEq.compareTo(lowerPairEq) > 0);
        assertTrue("Lower pair with second element is not taken as lower", lowerPairEq.compareTo(greaterPairEq) < 0);
    }

}