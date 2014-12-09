package to2.dice.controllers.poker;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import to2.dice.controllers.AbstractGameController;
import to2.dice.game.GameSettings;
import to2.dice.game.Player;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.Response;
import to2.dice.server.GameServer;

import java.util.List;

public class PokerGameController extends AbstractGameController {
    public PokerGameController(GameServer server, GameSettings settings, String creator) {
        super(server, settings, creator);
    }

    @Override
    public Player getWinner(List<Player> players){
        //TODO: function
        return null;
    }
}
