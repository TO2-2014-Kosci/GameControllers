package to2.dice.controllers.poker.hands;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import to2.dice.controllers.poker.HandFactory;
import to2.dice.game.Dice;

import java.util.Arrays;

import static org.junit.Assert.*;

public class HandFactoryTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testPoker() throws Exception {
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 5, 5, 5})), new Poker(5));
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{4, 4, 4, 4, 4})), new Poker(4));
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 5, 5, 1})) instanceof Poker);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{6, 2, 4, 1, 3})) instanceof Poker);
    }

    @Test
    public void testFour() throws Exception {
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{1, 2, 1, 1, 1})), new Four(1, 2));
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{3, 4, 3, 3, 3})), new Four(3, 4));
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 5, 5, 5})) instanceof Four);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{1, 5, 4, 3, 2})) instanceof Four);
    }

    @Test
    public void testFull() throws Exception {
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{3, 2, 3, 2, 3})), new Full(3, 2));
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{3, 4, 3, 4, 3})), new Full(3, 4));
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{3, 3, 3, 2, 1})) instanceof Full);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 1, 4, 3})) instanceof Full);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{3, 4, 2, 1, 5})) instanceof Full);
    }

    @Test
    public void testLargeStraight() throws Exception {
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{2, 4, 3, 6, 5})), new LargeStraight());
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{1, 2, 3, 4, 5})) instanceof LargeStraight);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{1, 3, 4, 5, 6})) instanceof LargeStraight);
    }

    @Test
    public void testSmallStraight() throws Exception {
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{1, 2, 3, 4, 5})), new SmallStraight());
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{2, 3, 4, 5, 6})) instanceof SmallStraight);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{1, 3, 4, 5, 6})) instanceof SmallStraight);
    }
    
    @Test
    public void testIsThree() throws Exception {
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{3, 3, 3, 2, 1})), new Three(3, new int[]{2, 1}));
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{1, 2, 2, 6, 2})), new Three(2, new int[]{6, 1}));
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{3, 3, 3, 2, 2})) instanceof Three);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 5, 5, 5})) instanceof Three);
    }

    @Test
    public void testIsTwoPairs() throws Exception {
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{2, 2, 4, 5, 5})), new TwoPairs(2, 5, 4));
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{5, 4, 2, 5, 4})), new TwoPairs(5, 4, 2));
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{1, 2, 3, 4, 5})) instanceof TwoPairs);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{3, 3, 3, 3, 1})) instanceof TwoPairs);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 5, 5, 5})) instanceof TwoPairs);
    }

    @Test
    public void testIsPair() throws Exception {
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{3, 4, 2, 1, 4})), new Pair(4, new int[]{3, 2, 1}));
        assertEquals(HandFactory.createHandFromDice(new Dice(new int[]{2, 3, 2, 5, 6})), new Pair(2, new int[]{3, 6, 5}));
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 5, 5, 5})) instanceof Pair);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 5, 5, 1})) instanceof Pair);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 3, 3, 5})) instanceof Pair);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{1, 2, 3, 4, 5})) instanceof Pair);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{2, 3, 2, 1, 3})) instanceof Pair);
    }

    @Test
    public void testHighCard() throws Exception {
        assertEquals(HandFactory.createHandFromDice(new Dice((new int[]{1, 2, 3, 5, 6}))), new HighCard(new int[]{1, 2, 3, 5, 6}));
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{3, 4, 2, 1, 4})) instanceof HighCard);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{2, 2, 4, 5, 5})) instanceof HighCard);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{1, 2, 3, 4, 5})) instanceof HighCard);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{2, 4, 3, 6, 5})) instanceof HighCard);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{3, 2, 3, 2, 3})) instanceof HighCard);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{1, 2, 1, 1, 1})) instanceof HighCard);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{5, 5, 5, 5, 5})) instanceof HighCard);
        assertFalse(HandFactory.createHandFromDice(new Dice(new int[]{3, 3, 3, 2, 1})) instanceof HighCard);

    }
}