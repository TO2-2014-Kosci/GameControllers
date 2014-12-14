package to2.dice.controllers.poker.hands;

public class Four extends Hand {
    private int fourValue;
    private int otherValue;

    public Four(int fourValue, int otherValue) {
        super(HandType.FOUR);
        this.fourValue = fourValue;
        this.otherValue = otherValue;
    }

    public int getFourValue() {
        return fourValue;
    }

    public int getOtherValue() {
        return otherValue;
    }

    @Override
    public int compareTo(Hand givenHand) {
        if (givenHand.getType() != this.type) {
            return super.compareTo(givenHand);
        } else {
            Four givenFour = (Four) givenHand;
            if (this.fourValue != (givenFour.getFourValue())) {
                return this.fourValue - givenFour.getFourValue();
            } else {
                return this.otherValue - givenFour.getOtherValue();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Four four = (Four) o;

        if (fourValue != four.fourValue) return false;
        if (otherValue != four.otherValue) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fourValue;
        result = 31 * result + otherValue;
        return result;
    }
}
