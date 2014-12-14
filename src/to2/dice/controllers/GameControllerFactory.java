package to2.dice.controllers;

import to2.dice.controllers.ngames.strategies.MulCountingStrategy;
import to2.dice.controllers.ngames.NGameController;
import to2.dice.controllers.ngames.strategies.PlusCountingStrategy;
import to2.dice.controllers.poker.PokerGameController;
import to2.dice.game.GameSettings;
import to2.dice.server.GameServer;

public final class GameControllerFactory {
    private GameControllerFactory() {
    }

    public static GameController createGameControler(GameServer server, GameSettings settings, String creator) {
        switch (settings.getGameType()) {
            case NPLUS:
                return new NGameController(server, settings, creator, new PlusCountingStrategy());
            case NMUL:
                return new NGameController(server, settings, creator, new MulCountingStrategy());
            case POKER:
                return new PokerGameController(server, settings, creator);
            default:
                return null;
        }
    }
}