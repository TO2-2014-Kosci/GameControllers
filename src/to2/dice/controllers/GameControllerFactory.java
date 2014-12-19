package to2.dice.controllers;

import to2.dice.controllers.ngames.strategies.MulCountingStrategy;
import to2.dice.controllers.ngames.NGameController;
import to2.dice.controllers.ngames.strategies.PlusCountingStrategy;
import to2.dice.controllers.poker.PokerGameController;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.NGameState;
import to2.dice.server.GameServer;

public final class GameControllerFactory {
    private GameControllerFactory() {
    }

    public static GameController createGameControler(GameServer server, GameSettings settings, String creator) {
        switch (settings.getGameType()) {
            case NPLUS:
                return new NGameController(server, settings, creator, new PlusCountingStrategy(), new NGameState());
            case NMUL:
                return new NGameController(server, settings, creator, new MulCountingStrategy(), new NGameState());
            case POKER:
                return new PokerGameController(server, settings, creator, new GameState());
            default:
                return null;
        }
    }
}