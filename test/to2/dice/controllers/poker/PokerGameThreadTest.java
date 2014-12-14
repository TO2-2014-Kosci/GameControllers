package to2.dice.controllers.poker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import to2.dice.ai.Bot;
import to2.dice.controllers.GameController;
import to2.dice.controllers.common.RoomController;
import to2.dice.game.*;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.Response;
import to2.dice.server.GameServer;

import java.util.*;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class PokerGameThreadTest {

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
    private final int playersNumber = 4;
    private final int timeForMove = 10;
    private final int maxInactiveTurns = 3;
    private final int roundsToWin = 5;
    private RoomController roomController;
    private GameServer server;
    private GameState state;
    private GameSettings settings;
    private Queue<SendToAllRequest> sentRequests;
    private GameController gameController;
    private PokerGameThread gameThread;
    private HashMap<Player, Bot> bots;
    private HashMap<BotLevel, Integer> botsNumber;
    private Player firstPlayer;
    Map<Player, int[]> previousDiceArray = new HashMap<>();

    @Before
    public void setUp() throws Exception {

        bots = new HashMap<Player, Bot>();
        botsNumber = new HashMap<BotLevel, Integer>();
        settings = new GameSettings(GameType.POKER, 5, "Krakow", playersNumber, timeForMove, maxInactiveTurns, roundsToWin, botsNumber);

        state = new GameState();

        firstPlayer = new Player("Pierwszy", false, settings.getDiceNumber());
        state.addPlayer(firstPlayer);
        state.addPlayer(new Player("Drugi", false, settings.getDiceNumber()));
        state.addPlayer(new Player("Trzeci", false, settings.getDiceNumber()));
        state.addPlayer(new Player("Czwarty", false, settings.getDiceNumber()));

        sentRequests = new LinkedList<SendToAllRequest>();
        server = new GameServer() {
            @Override
            public void sendToAll(GameController sender, GameState state) {
                sentRequests.add(new SendToAllRequest(sender, state));
            }
            @Override
            public void finishGame(GameController sender) {
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

        gameThread = new PokerGameThread(server, gameController, settings, state, bots) {

        };
        roomController = new RoomController(server, gameController, settings, state, bots);

        gameThread.setRoomController(roomController);
        //roomController.setGameThread(gameThread);
        gameThread.start();
        sleep(2000);
    }

    @Test
    public void testStart() throws Exception {
        synchronized (roomController) {
            assertFalse("Starting game sends too many GameStates", sentRequests.size() > 1);
            assertFalse("Starting game does not send GameState to all", sentRequests.isEmpty());
            SendToAllRequest request = sentRequests.poll();
            assertEquals("Starting game sends wrong controller handler", gameController, request.getController());
            GameState requestState = request.getState();
            assertTrue("Starting game sends GameState with no gameStarted set", requestState.isGameStarted());
            assertNotNull("Starting game sends GameState with no currentPlayer set", requestState.getCurrentPlayer());
            assertEquals("Starting game sends GameState with currentPlayer not set as first player", firstPlayer.getName(), requestState.getCurrentPlayer().getName());
            assertEquals("Starting game sends GameState with currentRound not set as first round", 1, requestState.getCurrentRound());

            for (Player player : requestState.getPlayers()) {
                assertNotNull("Starting game sends GameState with not set player dice", player.getDice());
                assertNotNull("Starting game sends GameState with not set dice array", player.getDice().getDiceArray());
                assertEquals("Starting game sends GameState with wrong dice array format", settings.getDiceNumber(), player.getDice().getDiceArray().length);
            }
        }
    }

    @Test
    public void testHumanGameScenario() throws Exception {
        List<Player> players;
        synchronized (roomController) {
            players = state.getPlayers();
        }
        Random r = new Random();

        for (Player player : state.getPlayers()) {
            previousDiceArray.put(player, new int[]{0, 0, 0, 0, 0});
        }
        boolean[] chosenDice = new boolean[]{false, false, false, false, false};


        for (int roundNumber = 1; roundNumber <= roundsToWin; roundNumber++) {
            for (int rerollNumber = 1; rerollNumber <= gameThread.getRerollsNumber(); rerollNumber++) {
                for (Player player : players) {
                    String message = "Round: " + Integer.toString(roundNumber) + "; Reroll: " + Integer.toString(rerollNumber) + "; Player \"" + player.getName() + "\" needs GameState. ";
                    System.out.print(message);
                    GameState requestState = getState(message);

                    assertNotNull(message + "GameState with no currentPlayer set", requestState.getCurrentPlayer());
                    assertEquals(message + "GameState with wrong currentPlayer set", player.getName(), requestState.getCurrentPlayer().getName());
                    assertEquals(message + "GameState with wrong currentRound set", roundNumber, requestState.getCurrentRound());

                    checkDiceRerolling(message, rerollNumber, chosenDice, requestState);
                    System.out.print("GameState is OK. ");

                    chosenDice = new boolean[]{r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean()};
                    gameThread.handleRerollRequest(chosenDice);
                    sleep(100);
                    System.out.println();
                }
            }
        }

        GameState requestState = getState("Last.");
        checkDiceRerolling("Last. ", 3, chosenDice, requestState);
        assertFalse("Finishing GameState with no isGameStarted set to false", requestState.isGameStarted());


    }

    private GameState getState(String message) {
        assertFalse(message + "Too many GameStates sent", sentRequests.size() > 1);
        assertNotEquals(message + "GameState has not been sent", 0, sentRequests.size());
        System.out.print("GameState delivered. ");

        SendToAllRequest request = sentRequests.poll();
        return request.getState();
    }

    private void checkDiceRerolling(String message, int rerollNumber, boolean[] chosenDice, GameState requestState) {
        int[] current = requestState.getCurrentPlayer().getDice().getDiceArray();
        int[] previous = previousDiceArray.get(requestState.getCurrentPlayer());
        if (rerollNumber != 1) {
            for (int i = 0; i < current.length; i++) {
                if (!chosenDice[i]) {
                    assertEquals(message + "Unchosen dice number " + Integer.toString(i + 1) + " has changed value", previous[i], current[i]);
                }
            }
        }
        previousDiceArray.put(requestState.getCurrentPlayer(), current);
    }

    @After
    public void tearDown() throws Exception {
        gameThread.interrupt();

    }
}