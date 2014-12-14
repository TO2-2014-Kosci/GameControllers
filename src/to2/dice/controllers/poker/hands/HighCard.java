package to2.dice.controllers.poker.hands;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HighCard highCard = (HighCard) o;

        if (!Arrays.equals(values, highCard.values)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
