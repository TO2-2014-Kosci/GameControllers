package to2.dice.controllers.poker.hands;

public class Poker extends Hand {
    private int value;

    public Poker(int value) {
        super(HandType.POKER);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(Hand hand) {
        if (hand.getType() != this.type) {
            return super.compareTo(hand);
        } else {
            return this.value - ((Poker) hand).getValue();
        }
    }
}
