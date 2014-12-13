package to2.dice.controllers.poker.hands;

public class TwoPairs extends Hand {
    private int greaterPairValue;
    private int lowerPairValue;
    private int otherValue;

    public TwoPairs(int firstPairValue, int secondPairValue, int otherValue) {
        super(HandType.TWO_PAIRS);
        if (firstPairValue >= secondPairValue) {
            this.greaterPairValue = firstPairValue;
            this.lowerPairValue = secondPairValue;
        } else {
            this.greaterPairValue = secondPairValue;
            this.lowerPairValue = firstPairValue;
        }
        this.otherValue = otherValue;
    }

    public int getGreaterPairValue() {
        return greaterPairValue;
    }

    public int getLowerPairValue() {
        return lowerPairValue;
    }

    public int getOtherValue() {
        return otherValue;
    }

    @Override
    public int compareTo(Hand hand) {
        if (this.type != hand.getType()) {
            return super.compareTo(hand);
        } else {
            TwoPairs givenTwoPairs = (TwoPairs) hand;
            if (this.greaterPairValue != givenTwoPairs.getGreaterPairValue()) {
                return this.greaterPairValue - givenTwoPairs.getGreaterPairValue();
            } else if (this.lowerPairValue != givenTwoPairs.getLowerPairValue()) {
                return this.lowerPairValue - givenTwoPairs.getLowerPairValue();
            } else {
                return this.otherValue - givenTwoPairs.getOtherValue();
            }
        }
    }
}
