package to2.dice.controllers.common;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import to2.dice.ai.Bot;
import to2.dice.controllers.GameController;
import to2.dice.game.*;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.Response;
import to2.dice.server.GameServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class RoomControllerTest {

    private class SendToAllRequest {

        private GameController controller;

        private GameState state;
        public SendToAllRequest(GameController controller, GameState state) {
            this.controller = controller;
            this.state = state;
        }
        public GameController getController() {
            return controller;
        }

        public GameState getState() {
            return state;
        }

    }

    private RoomController roomController;
    private GameServer server;
    private GameState state;
    private GameSettings settings;
    private List<SendToAllRequest> sentRequests;
    private GameController gameController;
    private GameThread gameThread;
    private boolean gameStarted;
    private boolean gameFinished;
    private HashMap<Player, Bot> bots;
    private HashMap<BotLevel, Integer> botsNumber;

    @Before
    public void setUp() throws Exception {

        bots = new HashMap<Player, Bot>();
        botsNumber = new HashMap<BotLevel, Integer>();
        state = new GameState();
        settings = new GameSettings(GameType.POKER, 5, "Krakow", 4, 10, 3, 5, botsNumber);

        sentRequests = new ArrayList<SendToAllRequest>();
        gameStarted = false;
        gameFinished = false;

        server = new GameServer() {
            @Override
            public void sendToAll(GameController sender, GameState state) {
                sentRequests.add(new SendToAllRequest(sender, state));
            }
            @Override
            public void finishGame(GameController sender) {
                gameFinished = true;
            }
        };

        gameController = new GameController() {
        @Override
        public GameInfo getGameInfo() {
            return null;
        }

        @Override
        public Response handleGameAction(GameAction gameAction) {
            return null;
        }
    };

        roomController = new RoomController(server, gameController, settings, state, bots);
        gameThread = new GameThread(server, gameController, settings, state, bots) {
            @Override
            public void start() {
                gameStarted = true;
            }
        };

        roomController.setGameThread(gameThread);
        gameThread.setRoomController(roomController);
    }



    @Test
    public void testIsObserverWithName() throws Exception {
        String firstObserverName = "Janusz";
        String secondObserverName = "Janek";

        roomController.addObserver(firstObserverName);
        assertTrue("Added observer is not present", roomController.isObserverWithName(firstObserverName));
        assertFalse("Non-added observer is present", roomController.isObserverWithName(firstObserverName + Integer.toString(1)));
        roomController.addObserver(secondObserverName);
        assertTrue("Second added observer is not present", roomController.isObserverWithName(secondObserverName));
        assertTrue("First added observer is not present after adding second", roomController.isObserverWithName(firstObserverName));
        roomController.removeObserver(firstObserverName);
        assertFalse("Removed observer is present", roomController.isObserverWithName(firstObserverName));
        assertTrue("First added observer after removal second is not present", roomController.isObserverWithName(secondObserverName));
    }

    @Test
    public void testAddPlayer() throws Exception {
        String playerName = "Wojciech";
        roomController.addPlayer(playerName);
        assertFalse("Adding player sends too many GameStates", sentRequests.size() > 1);
        assertFalse("Adding player does not send GameState to all", sentRequests.isEmpty());
        SendToAllRequest request = sentRequests.get(0);
        assertTrue("Adding player sends wrong controller handler", request.getController() == gameController);
        assertTrue("Adding player sends GameState with no information about added player", request.getState().isPlayerWithName(playerName));
    }

    @Test
    public void testRemovePlayer() throws Exception {
        String playerName = "Marek";
        roomController.addPlayer(playerName);
        roomController.removePlayer(playerName);
        assertFalse("Removing player malfuntion", roomController.isPlayerWithName(playerName));
    }

    @Test
    public void testIsPlayerWithName() throws Exception {
        String firstPlayerName = "Janusz";
        String secondPlayerName = "Janek";

        roomController.addPlayer(firstPlayerName);
        assertTrue("Added player is not present", roomController.isPlayerWithName(firstPlayerName));
        assertFalse("Non-added player is present", roomController.isPlayerWithName(firstPlayerName + Integer.toString(1)));
        roomController.addPlayer(secondPlayerName);
        assertTrue("Second added player is not present", roomController.isPlayerWithName(secondPlayerName));
        assertTrue("First added player is not present after adding second", roomController.isPlayerWithName(firstPlayerName));
        roomController.removePlayer(firstPlayerName);
        assertFalse("Removed player is present", roomController.isPlayerWithName(firstPlayerName));
        assertTrue("First added player after removal second is not present", roomController.isPlayerWithName(secondPlayerName));
    }

    @Test
    public void testAddBot() throws Exception {

    }

    @Test
    public void testIsRoomFull() throws Exception {

        for (int i = 0; i < settings.getMaxPlayers(); i++) {
            roomController.addPlayer(Integer.toString(i));
            if (i == settings.getMaxPlayers()/2) {
                assertFalse("Not full room is reported as full", roomController.isRoomFull());
            }
        }
        assertTrue("Full room is reported as not full", roomController.isRoomFull());
    }

    @Test
    public void testIsGameStarted() throws Exception {
        for (int i = 0; i < settings.getMaxPlayers(); i++) {
            roomController.addPlayer(Integer.toString(i));
            }
        assertTrue("Game with maximum players number is not started", gameStarted);
    }



    @After
    public void tearDown() throws Exception {


    }
}