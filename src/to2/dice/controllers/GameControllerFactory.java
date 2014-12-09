package to2.dice.controllers;

import to2.dice.controllers.ngames.*;
import to2.dice.game.GameSettings;
import to2.dice.server.GameServer;

public final class GameControllerFactory {
    private GameControllerFactory() {
    }

    public static AbstractGameController createGameControler(GameServer server, GameSettings settings, String creator) {
        switch (settings.getGameType()) {
            case NPLUS:
                return new to2.dice.controllers.ngames.NGameController(server, settings, creator, new PlusCountingStrategy());
            case NMUL:
                return new to2.dice.controllers.ngames.NGameController(server, settings, creator, new MulCountingStrategy());
            case POKER:
                return new to2.dice.controllers.poker.PokerGameController(server, settings, creator);
            default:
                return null;
        }
    }
}