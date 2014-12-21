package to2.dice.controllers.poker;

import to2.dice.controllers.poker.hands.Hand;
import to2.dice.game.Player;

class PlayerHand implements Comparable<PlayerHand> {
    private final Player player;
    private final Hand hand;

    PlayerHand(Player player, Hand hand) {
        this.player = player;
        this.hand = hand;
    }

    public Player getPlayer() {
        return player;
    }

    public Hand getHand() {
        return hand;
    }


    @Override
    public int compareTo(PlayerHand playerHand) {
        return this.hand.compareTo(playerHand.getHand());
    }
}
