package to2.dice.controllers.ngames;

import to2.dice.ai.Bot;
import to2.dice.controllers.GameController;
import to2.dice.controllers.common.GameThread;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;
import to2.dice.server.GameServer;

import java.util.Map;

public class NGameThread extends GameThread {
    public NGameThread(GameServer server, GameController gameController, GameSettings settings, GameState state, Map<Player, Bot> bots) {
        super(server, gameController, settings, state, bots);
    }
}
