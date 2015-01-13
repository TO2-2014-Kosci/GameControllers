package to2.dice.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import to2.dice.ai.Bot;
import to2.dice.game.GameInfo;
import to2.dice.game.GameState;
import to2.dice.game.Player;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.GameActionType;
import to2.dice.messaging.RerollAction;
import to2.dice.messaging.Response;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class BotsAgentTest {


    private Player unregisteredBotPlayer;
    private Player secondBotPlayer;

    class MyBot extends Bot {
        private boolean returnedValue;

        public MyBot(boolean returnedValue) {
            this.returnedValue = returnedValue;
        }

        @Override
        protected void chooseResult(int[] ints, List<int[]> list) {
        }

        @Override
        public boolean[] makeMove(int[] dice, List<int[]> otherDice) {
            Random r = new Random();
            return new boolean[]{returnedValue, returnedValue, returnedValue, returnedValue, returnedValue};
        }
    }

    private List<GameAction> actions;
    private GameController controller;
    private BotsAgent agent;
    private Player botPlayer;
    private Player humanPlayer;
    private MyBot bot;
    private GameState state;


    @Before
    public void setUp() throws Exception {
        actions = new LinkedList<>();

        controller = new GameController() {
            @Override
            public GameInfo getGameInfo() {
                return null;
            }

            @Override
            public Response handleGameAction(GameAction gameAction) {
                actions.add(gameAction);
                return new Response(Response.Type.SUCCESS);
            }
        };

        humanPlayer = new Player("Human", false, 5);
        botPlayer = new Player("Bot", true, 5);
        unregisteredBotPlayer = new Player("UnregisteredBot", true, 5);
        secondBotPlayer = new Player("SecondBot", true, 5);

        agent = new BotsAgent(controller);
        agent.registerBot(botPlayer, new MyBot(true));
        agent.registerBot(secondBotPlayer, new MyBot(false));

        state = new GameState();
        state.addPlayer(humanPlayer);
        state.addPlayer(botPlayer);
        state.addPlayer(unregisteredBotPlayer);
        state.setGameStarted(true);
    }

    @Test
    public void testBotsAgentReaction() throws Exception {
        state.setCurrentPlayer(humanPlayer);

        agent.processNewGameState(state);
        sleep(2000);
        assertTrue("Agent processed human GameState", actions.isEmpty());

        state.setCurrentPlayer(botPlayer);

        agent.processNewGameState(state);
        sleep(2000);
    //    assertTrue("Agent have not processed bot GameState", actions.size() == 1);

        GameAction sentAction = actions.remove(0);
        assertEquals("Agent sent GameAction with wrong type", GameActionType.REROLL, sentAction.getType());
        for (boolean value : ((RerollAction)sentAction).getChosenDice()) {
            assertEquals("Agent use wrong bot instance", true, value);
        }

        state.setCurrentPlayer(unregisteredBotPlayer);
        agent.processNewGameState(state);
        sleep(2000);
        assertTrue("Agent processed unregistered bot GameState", actions.isEmpty());
    }


    @After
    public void tearDown() throws Exception {


    }
}