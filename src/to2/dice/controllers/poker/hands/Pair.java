package to2.dice.controllers.poker.hands;

import java.util.Arrays;

public class Pair extends Hand {

    private int pairValue;
    private int[] otherValues;

    public Pair(int pairValue, int[] otherValues) {
        super(HandType.PAIR);
        this.pairValue = pairValue;
        this.otherValues = otherValues;
        sort(this.otherValues);
    }

    public int getPairValue() {
        return pairValue;
    }

    public int[] getOtherValues() {
        return otherValues;
    }

    @Override
    public int compareTo(Hand hand) {
        if (this.type != hand.getType()) {
            return super.compareTo(hand);
        } else {
            Pair givenPair = (Pair) hand;
            if (this.pairValue != givenPair.getPairValue()) {
                return this.pairValue - givenPair.getPairValue();
            } else {
                return compareValues(this.otherValues, givenPair.getOtherValues());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Pair pair = (Pair) o;

        if (pairValue != pair.pairValue) return false;
        if (!Arrays.equals(otherValues, pair.otherValues)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + pairValue;
        result = 31 * result + Arrays.hashCode(otherValues);
        return result;
    }
}
