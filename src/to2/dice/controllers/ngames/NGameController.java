package to2.dice.controllers.ngames;

import to2.dice.controllers.AbstractGameController;
import to2.dice.game.GameSettings;
import to2.dice.game.NGameState;
import to2.dice.game.Player;
import to2.dice.server.GameServer;

import java.util.List;

public class NGameController extends AbstractGameController {
    private CountingStrategy strategy;

    public NGameController(GameServer server, GameSettings settings, NGameState state, String creator, CountingStrategy strategy) {
        super(server, settings, creator, state);
        this.strategy = strategy;
        setGameThread(new NGameThread(server, this, settings, state, bots, strategy));
    }
}