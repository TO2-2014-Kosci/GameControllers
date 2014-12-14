package to2.dice.controllers.poker.hands;

public abstract class Hand implements Comparable<Hand> {
    protected HandType type;

    public Hand(HandType type) {
        this.type = type;
    }

    public HandType getType() {
        return type;
    }


    public int compareTo(Hand hand) {
        return this.type.getValue() - hand.getType().getValue();
    }

    public static void sort(int[] dice) {
        for (int i = 0; i < dice.length; i++) {
            int min = i;

            for (int j = i + 1; j < dice.length; j++) {
                if (dice[j] < dice[min]) {
                    min = j;
                }
            }

            int tmp = dice[i];
            dice[i] = dice[min];
            dice[min] = tmp;
        }
    }

    public static boolean isEqual(Hand a, Hand b) {
        return a.equals(b);
    }

    public static boolean isGreater(Hand a, Hand b) {
        return a.compareTo(b) > 0;
    }

    public static boolean isGreaterOrEqual(Hand a, Hand b) {
        return a.compareTo(b) >= 0;
    }

    public static boolean isLesser(Hand a, Hand b) {
        return a.compareTo(b) < 0;
    }

    public static boolean isLesserOrEqual(Hand a, Hand b) {
        return a.compareTo(b) <= 0;
    }

    protected int compareValues(int[] firstArray, int[] secondArray) {
        if (firstArray.length != secondArray.length)
            throw new IllegalArgumentException();
        else {
            for (int i = 0; i < firstArray.length; i++) {
                if (firstArray[i] != secondArray[i])
                    return firstArray[i] - secondArray[i];
            }
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hand hand = (Hand) o;

        if (type != hand.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }
}
