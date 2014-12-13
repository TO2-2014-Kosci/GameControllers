package to2.dice.controllers.poker.hands;

public class HighCard extends Hand {
    private int[] values;

    public HighCard(int[] values) {
        super(HandType.HIGH_CARD);
        this.values = values;
        sort(this.values);
    }

    public int[] getValues() {
        return values;
    }

    @Override
    public int compareTo(Hand hand) {
        if (hand.getType() != this.type) {
            return super.compareTo(hand);
        } else {
            return compareValues(this.values, ((HighCard) hand).getValues());
        }
    }
}
