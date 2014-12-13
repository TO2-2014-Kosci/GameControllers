package to2.dice.controllers.poker.hands;

public class Pair extends Hand {

    private int pairValue;
    private int[] otherValues;

    public Pair(int pairValue, int[] otherValues) {
        super(HandType.PAIR);
        this.pairValue = pairValue;
        this.otherValues = otherValues;
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
}
