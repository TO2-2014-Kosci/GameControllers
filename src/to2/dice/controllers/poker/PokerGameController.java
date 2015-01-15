package to2.dice.controllers.poker;

import to2.dice.controllers.AbstractGameController;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.server.GameServer;

public class PokerGameController extends AbstractGameController {
    public PokerGameController(GameServer server, GameSettings settings, GameState state, String creator) {
        super(server, settings, creator);
        initialize(state, new PokerGameStrategy(settings, state));
    }
}
