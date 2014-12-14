package to2.dice.controllers.poker.hands;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Three three = (Three) o;

        if (threeValue != three.threeValue) return false;
        if (!Arrays.equals(otherValues, three.otherValues)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = threeValue;
        result = 31 * result + Arrays.hashCode(otherValues);
        return result;
    }
}
