package to2.dice.controllers.poker;

import to2.dice.controllers.GameStrategy;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;

import java.util.*;

public class PokerGameStrategy extends GameStrategy {

    public PokerGameStrategy(GameSettings settings, GameState state) {
        super(settings, state);
    }


    public Player getRoundWinner() {
        List<PlayerHand> playerHand = new ArrayList<PlayerHand>();
        for (Player player : state.getPlayers()) {
            playerHand.add(new PlayerHand(player, HandFactory.createHandFromDice(player.getDice())));
        }
        return Collections.max(playerHand).getPlayer();
    }


}
