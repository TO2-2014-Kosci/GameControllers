package to2.dice.controllers.poker.hands;

public enum HandType {
    HIGH_CARD(0),
    PAIR(1),
    TWO_PAIRS(2),
    THREE(3),
    SMALL_STRAIGHT(4),
    LARGE_STRAIGHT(5),
    FULL(6),
    FOUR(7),
    POKER(8);
    private final int level;

    private HandType(int level) {
        this.level = level;
    }

    public int getValue() {
        return level;
    }
}
