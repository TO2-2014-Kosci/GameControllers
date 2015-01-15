package to2.dice.controllers;

import org.junit.Before;
import org.junit.Test;
import to2.dice.controllers.AbstractGameController;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.*;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.GameActionType;
import to2.dice.server.GameServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class GameControllerActionTest {
    private GameState state;
    private GameServer server;
    private List<GameState> gameStatesList = new ArrayList<GameState>();

    public void setGameState(GameState gameState){
        this.state = gameState;
        gameStatesList.add(gameState);
    }

    @Before
    public void setUp() throws Exception {
        setGameState(null);
        gameStatesList.clear();

        server = new GameServer() {
            @Override
            public void sendToAll(GameController gameController, GameState gameState) {
                setGameState(gameState);
            }

            @Override
            public void finishGame(GameController gameController) {
                System.out.println("finishGame");
            }
        };

    }

    @Test
    public void testJoinAndStartNGame() throws Exception {
        GameSettings settings = new GameSettings(GameType.NPLUS, 5, "test", 1, 10, 2, 2, new HashMap<BotLevel, Integer>());

        GameController gameController = GameControllerFactory.createGameControler(server, settings, "Ntester");
        gameController.handleGameAction(new GameAction(GameActionType.JOIN_ROOM, "first"));
        gameController.handleGameAction(new GameAction(GameActionType.SIT_DOWN, "first"));
        assertTrue("Player (first) couldn't sit down", state.getPlayers().contains(new Player("first", false, 5)));

        assertTrue("NGame should be started", gameController.getGameInfo().isGameStarted());
    }

    @Test
    public void testJoinAndStartPoker() throws Exception{
        GameSettings settings = new GameSettings(GameType.POKER, 5, "test", 1, 10, 2, 2, new HashMap<BotLevel, Integer>());

        GameController gameController = GameControllerFactory.createGameControler(server, settings, "Pokertester");
        gameController.handleGameAction(new GameAction(GameActionType.JOIN_ROOM, "firstP"));
        gameController.handleGameAction(new GameAction(GameActionType.SIT_DOWN, "firstP"));
        assertTrue("Player (firstP) couldn't sit down", state.getPlayers().contains(new Player("firstP", false, 5)));

        assertTrue("PokerGame should be started", gameController.getGameInfo().isGameStarted());
    }
}