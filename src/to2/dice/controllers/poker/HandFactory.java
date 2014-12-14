package to2.dice.controllers.poker;


import to2.dice.controllers.poker.hands.*;
import to2.dice.game.Dice;

import java.lang.reflect.Array;
import java.util.Arrays;


public class HandFactory {

    public static Hand createHandFromDice(Dice dice) throws Exception {
        int[] diceArray = dice.getDiceArray();

        if (diceArray.length != 5) {
            throw new Exception();
        }

        Hand.sort(diceArray);

        Hand hand;

        if ((hand = getPoker(diceArray)) != null) {
            return hand;
        } else if ((hand = getFour(diceArray)) != null) {
            return hand;
        } else if ((hand = getFull(diceArray)) != null) {
            return hand;
        } else if ((hand = getLargeStraight(diceArray)) != null) {
            return hand;
        } else if ((hand = getSmallStraight(diceArray)) != null) {
            return hand;
        } else if ((hand = getThree(diceArray)) != null) {
            return hand;
        } else if ((hand = getTwoPairs(diceArray)) != null) {
            return hand;
        } else if ((hand = getPair(diceArray)) != null) {
            return hand;
        } else {
            return new HighCard(diceArray);
        }
    }

    private static Poker getPoker(int[] dice) {
        if ((dice[0] == dice[1]) && (dice[1] == dice[2]) && (dice[2] == dice[3]) && (dice[3] == dice[4])) {
            return new Poker(dice[0]);
        } else {
            return null;
        }
    }

    private static Four getFour(int[] dice) {
        if ((dice[0] == dice[1]) && (dice[1] == dice[2]) && (dice[2] == dice[3])) {
            return new Four(dice[0], dice[4]);
        } else if ((dice[1] == dice[2]) && (dice[2] == dice[3]) && (dice[3] == dice[4])) {
            return new Four(dice[1], dice[0]);
        } else {
            return null;
        }
    }

    private static Full getFull(int[] dice) {
        if ((dice[0] == dice[1]) && ((dice[2] == dice[3]) && (dice[3] == dice[4]))) {
            return new Full(dice[2], dice[0]);
        } else if ((dice[0] == dice[1]) && ((dice[1] == dice[2]) && (dice[3] == dice[4]))) {
            return new Full(dice[0], dice[3]);
        } else {
            return null;
        }
    }

    private static LargeStraight getLargeStraight(int[] dice) {
        if (Arrays.equals(dice, new int[]{2, 3, 4, 5, 6})) {
            return new LargeStraight();
        } else {
            return null;
        }
    }

    private static SmallStraight getSmallStraight(int[] dice) {
        if (Arrays.equals(dice, new int[]{1, 2, 3, 4, 5})) {
            return new SmallStraight();
        } else {
            return null;
        }
    }

    private static Three getThree(int[] dice) {
        if (dice[0] == dice[1] && dice[1] == dice[2]) {
            return new Three(dice[0], new int[]{dice[3], dice[4]});
        } else if (dice[1] == dice[2] && dice[2] == dice[3]) {
            return new Three(dice[1], new int[]{dice[0], dice[4]});
        } else if (dice[2] == dice[3] && dice[3] == dice[4]) {
            return new Three(dice[2], new int[]{dice[0], dice[1]});
        } else {
            return null;
        }
    }

    private static TwoPairs getTwoPairs(int[] dice) {
        if ((dice[0] == dice[1] && dice[3] == dice[4])) {
            return new TwoPairs(dice[0], dice[3], dice[2]);
        } else if ((dice[1] == dice[2] && dice[3] == dice[4])) {
            return new TwoPairs(dice[1], dice[3], dice[0]);
        } else if ((dice[0] == dice[1] && dice[2] == dice[3])) {
            return new TwoPairs(dice[0], dice[2], dice[4]);
        } else {
            return null;
        }
    }

    private static Pair getPair(int[] dice) {
        if (dice[0] == dice[1]) {
            return new Pair(dice[0], new int[]{dice[2], dice[3], dice[4]});
        } else if (dice[1] == dice[2]) {
            return new Pair(dice[1], new int[]{dice[0], dice[3], dice[4]});
        } else if (dice[2] == dice[3]) {
            return new Pair(dice[2], new int[]{dice[0], dice[1], dice[4]});
        } else if (dice[3] == dice[4]) {
            return new Pair(dice[3], new int[]{dice[0], dice[1], dice[2]});
        } else {
            return null;
        }
    }
}
