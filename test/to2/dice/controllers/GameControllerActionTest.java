package to2.dice.controllers;

import org.junit.Test;
import to2.dice.controllers.AbstractGameController;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.GameType;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.GameActionType;
import to2.dice.server.GameServer;

import java.util.HashMap;

import static org.junit.Assert.*;

public class GameControllerActionTest {
    private GameState state;

    public void setGameState(GameState gameState){
        this.state = gameState;
    }

    @Test
    public void testHandleGameActionNGame() throws Exception {
        setGameState(null);
        GameServer server = new GameServer() {
            @Override
            public void sendToAll(GameController gameController, GameState gameState) {
                setGameState(gameState);
            }

            @Override
            public void finishGame(GameController gameController) {
                System.out.println("finishGame");
            }
        };
        GameSettings settings = new GameSettings(GameType.NPLUS, 5, "test", 2, 10, 2, 2, new HashMap<BotLevel, Integer>());

        GameController gameController = GameControllerFactory.createGameControler(server, settings, "tester");
        gameController.handleGameAction(new GameAction(GameActionType.JOIN_ROOM, "first"));
        gameController.handleGameAction(new GameAction(GameActionType.SIT_DOWN, "first"));
        assertTrue("Player couldn't sit down", !state.getPlayers().isEmpty());
    }

    @Test
    public void testHandleGameActionPoker() throws Exception{
        setGameState(null);
        GameServer server = new GameServer() {
            @Override
            public void sendToAll(GameController gameController, GameState gameState) {
                setGameState(gameState);
            }

            @Override
            public void finishGame(GameController gameController) {
                System.out.println("finishGame");
            }
        };
        GameSettings settings = new GameSettings(GameType.POKER, 5, "test", 2, 10, 2, 2, new HashMap<BotLevel, Integer>());

        GameController gameController = GameControllerFactory.createGameControler(server, settings, "tester");
        gameController.handleGameAction(new GameAction(GameActionType.JOIN_ROOM, "first"));
        gameController.handleGameAction(new GameAction(GameActionType.SIT_DOWN, "first"));
        assertTrue("Player couldn't sit down", !state.getPlayers().isEmpty());
    }
}