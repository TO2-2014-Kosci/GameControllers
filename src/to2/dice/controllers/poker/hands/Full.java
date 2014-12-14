package to2.dice.controllers.poker.hands;

public class Full extends Hand {
    private int threeValue;
    private int pairValue;

    public Full(int threeValue, int pairValue) {
        super(HandType.FULL);
        this.threeValue = threeValue;
        this.pairValue = pairValue;
    }

    public int getThreeValue() {
        return threeValue;
    }

    public int getPairValue() {
        return pairValue;
    }

    @Override
    public int compareTo(Hand hand) {
        if (this.type != hand.getType()) {
            return super.compareTo(hand);
        } else {
            Full givenFull = (Full) hand;
            if (this.threeValue != givenFull.getThreeValue()) {
                return this.threeValue - givenFull.getThreeValue();
            } else {
                return this.pairValue - givenFull.getPairValue();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Full full = (Full) o;

        if (pairValue != full.pairValue) return false;
        if (threeValue != full.threeValue) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = threeValue;
        result = 31 * result + pairValue;
        return result;
    }
}
