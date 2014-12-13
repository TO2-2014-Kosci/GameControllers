package to2.dice.controllers.ngames;

import to2.dice.controllers.AbstractGameController;
import to2.dice.game.GameSettings;
import to2.dice.game.Player;
import to2.dice.server.GameServer;

import java.util.List;

public class NGameController extends AbstractGameController {
    private CountingStrategy strategy;

    public NGameController(GameServer server, GameSettings settings, String creator, CountingStrategy strategy) {
        super(server, settings, creator);
        this.strategy = strategy;
    }

    @Override
    public Player getWinner(List<Player> players) {
        //TODO: function
        return null;
    }
}