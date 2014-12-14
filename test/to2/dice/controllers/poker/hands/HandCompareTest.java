package to2.dice.controllers.poker.hands;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HandCompareTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCompare() throws Exception {
        assertTrue(Hand.isGreater(new HighCard(new int[]{1, 2, 4, 5, 6}), new HighCard(new int[]{1, 2, 3, 5, 6})));
        assertTrue(Hand.isLesser(new HighCard(new int[]{1, 2, 3, 5, 6}), new HighCard(new int[]{1, 2, 4, 5, 6})));
        assertTrue(Hand.isLesser(new HighCard(new int[]{1, 2, 3, 5, 6}), new Pair(2, new int[]{3, 6, 5})));

        assertTrue(Hand.isEqual(new Pair(2, new int[]{1, 5, 6}), new Pair(2, new int[]{6, 5, 1})));
        assertTrue(Hand.isGreater(new Pair(2, new int[]{3, 6, 5}), new Pair(2, new int[]{5, 1, 6})));
        assertTrue(Hand.isLesser(new Pair(2, new int[]{3, 6, 5}), new Pair(3, new int[] {5, 1, 6})));

        assertTrue(Hand.isGreater(new Three(3, new int[]{1, 6}), new HighCard(new int[]{1, 2, 3, 5, 6})));
        assertTrue(Hand.isGreater(new Three(3, new int[]{1, 6}), new Pair(5, new int[]{2, 3, 6})));
        assertTrue(Hand.isGreater(new Three(3, new int[]{1, 6}), new Three(3, new int[]{1, 2})));
        assertTrue(Hand.isLesser(new Three(3, new int[]{1, 6}), new Three(3, new int[]{6, 5})));
        assertTrue(Hand.isEqual(new Three(3, new int[]{1, 6}), new Three(3, new int[]{6, 1})));
        assertTrue(Hand.isLesser(new Three(3, new int[]{1, 6}), new Three(5, new int[]{1, 2})));
        assertTrue(Hand.isLesser(new Three(3, new int[]{1, 6}), new Three(5, new int[]{1, 2})));
        assertTrue(Hand.isLesser(new Three(3, new int[]{1, 6}), new Four(1, 2)));
        assertTrue(Hand.isLesser(new Three(3, new int[]{1, 6}), new Poker(6)));

        assertTrue(Hand.isGreater(new Four(3, 1), new Three(1, new int[]{2, 6})));
        assertTrue(Hand.isGreater(new Four(3, 1), new Four(1, 2)));
        assertTrue(Hand.isLesser(new Four(3, 1), new Poker(1)));
        assertTrue(Hand.isLesser(new Four(3, 1), new Four(4, 6)));

    }
}
