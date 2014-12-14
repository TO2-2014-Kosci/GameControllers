package to2.dice.controllers.poker;

import to2.dice.controllers.AbstractGameController;
import to2.dice.game.GameSettings;
import to2.dice.game.Player;
import to2.dice.server.GameServer;

import java.util.List;

public class PokerGameController extends AbstractGameController {
    public PokerGameController(GameServer server, GameSettings settings, String creator) {
        super(server, settings, creator);
        setGameThread(new PokerGameThread(server, this, settings, state, bots));
    }
}
