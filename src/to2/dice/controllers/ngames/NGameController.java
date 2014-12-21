package to2.dice.controllers.ngames;

import to2.dice.controllers.AbstractGameController;
import to2.dice.game.GameSettings;
import to2.dice.game.NGameState;
import to2.dice.server.GameServer;

public class NGameController extends AbstractGameController {
    public NGameController(GameServer server, GameSettings settings, NGameState state, String creator, CountingStrategy countingStrategy) {
        super(server, settings, creator);
        initialize(state, new NGameStrategy(settings, state, countingStrategy));
    }
}