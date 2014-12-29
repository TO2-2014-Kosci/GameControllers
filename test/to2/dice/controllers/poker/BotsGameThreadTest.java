package to2.dice.controllers.poker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import to2.dice.controllers.GameController;
import to2.dice.controllers.RoomController;
import to2.dice.game.*;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.Response;
import to2.dice.server.GameServer;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class BotsGameThreadTest {

    private class SendToAllRequest {

        private GameController controller;
        private String currentPlayerName;
        private int currentRoundNumber;

        public SendToAllRequest(GameController controller, GameState state) {
            this.controller = controller;
            this.currentPlayerName = state.getCurrentPlayer().getName();
            this.currentRoundNumber = state.getCurrentRound();
        }
        public GameController getController() {
            return controller;
        }

        public GameState getState() {
            return state;
        }

        public String getCurrentPlayerName() {
            return currentPlayerName;
        }

        public int getCurrentRoundNumber() {
            return currentRoundNumber;
        }
    }

    private class Bot extends to2.dice.ai.Bot {
        @Override
        protected void chooseResult(int[] ints, List<int[]> list) {
        }

        public boolean[] makeMove(int[] dice, List<int[]> otherDice) {
            Random r = new Random();
            return new boolean[]{r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean(), r.nextBoolean()};
        }
    }

    private final int roundsToWin = 5;
    private boolean isFinished = false;
    private RoomController roomController;
    private GameServer server;
    private GameState state;
    private GameSettings settings;
    private BlockingQueue<SendToAllRequest> sentRequest;
    private PokerGameThread gameThread;
    Map<Player, int[]> previousDiceArray = new HashMap<>();

    @Before
    public void setUp() throws Exception {

        HashMap<Player, to2.dice.ai.Bot> bots = new HashMap<Player, to2.dice.ai.Bot>();
        HashMap<BotLevel, Integer> botsNumber = new HashMap<BotLevel, Integer>();
        int playersNumber = 4;
        int timeForMove = 10;
        int maxInactiveTurns = 3;
        settings = new GameSettings(GameType.POKER, 5, "Krakow", playersNumber, timeForMove, maxInactiveTurns, roundsToWin, botsNumber);

        state = new GameState();

        Player firstPlayer = new Player("Pierwszy", true, settings.getDiceNumber());
        Player secondPlayer = new Player("Drugi", true, settings.getDiceNumber());
        Player thirdPlayer = new Player("Trzeci", true, settings.getDiceNumber());
        Player fourthPlayer = new Player("Czwarty", true, settings.getDiceNumber());

        state.addPlayer(firstPlayer);
        state.addPlayer(secondPlayer);
        state.addPlayer(thirdPlayer);
        state.addPlayer(fourthPlayer);

        bots.put(firstPlayer, new Bot());
        bots.put(secondPlayer, new Bot());
        bots.put(thirdPlayer, new Bot());
        bots.put(fourthPlayer, new Bot());


        sentRequest = new LinkedBlockingQueue<>();
        server = new GameServer() {
            @Override
            public synchronized void sendToAll(GameController sender, GameState state) {
                try {
                    sentRequest.put(new SendToAllRequest(sender, state));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!state.isGameStarted()) {
                    isFinished = true;
                    server.notify();
                }
            }
            @Override
            public void finishGame(GameController sender) {
            }
        };

        GameController gameController = new GameController() {
            @Override
            public GameInfo getGameInfo() {
                return null;
            }

            @Override
            public Response handleGameAction(GameAction gameAction) {
                return null;
            }
        };

        gameThread = new PokerGameThread(server, gameController, settings, state, bots);
        roomController = new RoomController(server, gameController, settings, state, bots);

        gameThread.setRoomController(roomController);
        roomController.setGameThread(gameThread);
        gameThread.start();
    }

    @Test
    public void testBotsGameScenario() throws Exception {
        synchronized (server) {
            while (!isFinished) {
                server.wait();
            }
        }
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
                    SendToAllRequest requestState = checkState(message);
                    assertEquals(message + "GameState with wrong currentPlayer set", player.getName(), requestState.getCurrentPlayerName());
                    assertEquals(message + "GameState with wrong currentRound set", roundNumber, requestState.getCurrentRoundNumber());

                    //checkDiceRerolling(message, rerollNumber, chosenDice, requestState);
                    System.out.print("GameState is OK. ");

                    System.out.println();
                }
            }
        }

//        GameState requestState = checkState("Last.");
//        checkDiceRerolling("Last. ", 3, chosenDice, requestState);
//        assertFalse("Finishing GameState with no isGameStarted set to false", requestState.isGameStarted());
//        for (Player player : requestState.getPlayers()) {
//            System.out.print(player.getName() + ": " + Integer.toString(player.getScore()) + "; ");
//        }
//        System.out.println();
//        for (SendToAllRequest request : sentRequest) {
//            System.out.println(request.getState().getCurrentPlayer().getName() + " + " + request.getState().getCurrentRound());
//
//        }

    }

    private SendToAllRequest checkState(String message) {
        SendToAllRequest request = null;
        try {
            request = sentRequest.take();
            System.out.print("GameState delivered. ");
            return request;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotEquals(message + "GameState has not been sent", 0, sentRequest.size());
        return null;
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