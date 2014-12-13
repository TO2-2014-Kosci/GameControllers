package to2.dice.controllers.poker.hands;

public class Three extends Hand {
    private int threeValue;
    private int[] otherValues;

    public Three(int threeValue, int[] otherValues) {
        super(HandType.THREE);
        this.threeValue = threeValue;
        this.otherValues = otherValues;
        sort(this.otherValues);
    }

    public int getThreeValue() {
        return threeValue;
    }

    public int[] getOtherValues() {
        return otherValues;
    }

    @Override
    public int compareTo(Hand hand) {
        if (this.type != hand.getType()) {
            return super.compareTo(hand);
        } else {
            Three givenThree = (Three) hand;
            if (this.threeValue != givenThree.getThreeValue()) {
                return this.threeValue - givenThree.getThreeValue();
            } else {
                return compareValues(this.otherValues, givenThree.getOtherValues());
            }
        }
    }
}
