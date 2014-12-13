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
}
