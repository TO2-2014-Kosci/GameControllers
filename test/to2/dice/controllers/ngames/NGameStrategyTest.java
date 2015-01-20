package to2.dice.controllers.ngames;

import org.junit.Before;
import org.junit.Test;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameStrategy;
import to2.dice.controllers.MoveTimer;
import to2.dice.controllers.RoomController;
import to2.dice.controllers.ngames.strategies.PlusCountingStrategy;
import to2.dice.game.*;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.*;

public class NGameStrategyTest {
    private Player[] players = {new Player("1", false, 5), new Player("2", false, 5)};
    private MoveTimer moveTimer;
    private NGameState state;

    @Before
    public void setUp() throws Exception{
        state = new NGameState();
        for(Player p: players){
            state.addPlayer(p);
        }
    }

    @Test
    public void testStartGame() throws Exception {
        System.out.println("TESTING GAME LOGIC IN NGAMESTRATEGY");

        GameSettings settings = new GameSettings(GameType.NPLUS, 5, "test", 2, 10, 2, 2, new HashMap<BotLevel, Integer>());
        CountingStrategy countingStrategy = new PlusCountingStrategy();
        GameStrategy gameStrategy = new NGameStrategy(settings, state, countingStrategy);

        RoomController roomController = new RoomController(null, settings, null, gameStrategy){
            @Override
            public void updateGameState(){}

            @Override
            public void handleEndOfTimeRequest(){}
        };
        gameStrategy.setRoomController(roomController);
        ((NGameStrategy)gameStrategy).setAfterRerollWaitTime(1);

        assertTrue("Game shouldn't be started", !state.isGameStarted());
        assertTrue("Round shouldn't be started", state.getCurrentRound() == 0);
        gameStrategy.startGame();
        assertTrue("Game should be started", state.isGameStarted());
        assertTrue("It should be first round", state.getCurrentRound() == 1);

        int curr = 0;
        while(players[curr] != state.getCurrentPlayer()) curr++;

        int[] currDice = players[curr].getDice().getDiceArray().clone();
        boolean[] chosenDice = new boolean[settings.getDiceNumber()];
        java.util.Arrays.fill(chosenDice, false);
        chosenDice[0] = true;
        gameStrategy.reroll(chosenDice);
        int[] newDice = players[curr].getDice().getDiceArray();
        boolean diceDiff = true;
        for(int i = 0; i<settings.getDiceNumber(); i++){
            if(chosenDice[i]){
                if(currDice[i] == newDice[i]){
                    diceDiff = false;
                    break;
                }
            }
            else{
                if(currDice[i] != newDice[i]){
                    diceDiff = false;
                    break;
                }
            }
        }

        assertTrue("Only first dice should be different (because of random, can sometimes give err)", diceDiff);

        if(curr == 0) curr = 1;
        else curr = 0;
        assertTrue("It should be next player, after current, playing", state.getCurrentPlayer() == players[curr]);
    }

    @Test
    public void newRoundAndfinishGame() throws Exception{
        System.out.println("TESTING NGAMESTRATEGY ENDING GAME");
        int roundsNum = 30;

        GameSettings settings = new GameSettings(GameType.NPLUS, 5, "test", 2, 10, 2, roundsNum, new HashMap<BotLevel, Integer>());

        /* creating new Counting strategy without random */
        CountingStrategy countingStrategy = new CountingStrategy() {
            @Override
            public int countPoints(Dice dice) {
                return 5;
            }

            @Override
            public int generateWinningNumber(int diceNumber) {
                return 6;
            }
        };

        GameStrategy gameStrategy = new NGameStrategy(settings, state, countingStrategy);
        RoomController roomController = new RoomController(null, settings, null, gameStrategy){
            @Override
            public void updateGameState(){}

            @Override
            public void handleEndOfTimeRequest(){}
        };
        gameStrategy.setRoomController(roomController);
        ((NGameStrategy)gameStrategy).setAfterRerollWaitTime(1);

        gameStrategy.startGame();
        assertTrue("Game should be started", state.isGameStarted());

        boolean[] chosenDice = new boolean[settings.getDiceNumber()];
        java.util.Arrays.fill(chosenDice, false);

        while(state.isGameStarted()) {
            /* Setting sure win number for the second player*/
            if(state.getCurrentPlayer() == players[1]) state.setWinningNumber(5);
            assertTrue("Game should still be active", state.isGameStarted());
            gameStrategy.reroll(chosenDice);
        }
        assertTrue("Only second player should have points", players[0].getScore() == 0 && players[1].getScore() == roundsNum);
        assertTrue("Game should end", !state.isGameStarted());
    }


    @Test
    public void playersOrderTest() throws Exception{
        System.out.println("TESTING PLAYERS ORDER");
        int numberOfPlayers = 10;
        state = new NGameState();
        Player currPlayer;
        ArrayList<Player> newPlayers = new ArrayList<>();
        for(int i = 0; i<numberOfPlayers; i++){
            newPlayers.add(new Player(String.valueOf(i), false, 5));
            state.addPlayer(newPlayers.get(i));
        }

        GameSettings settings = new GameSettings(GameType.NPLUS, 5, "test", 2, 10, 2, 2, new HashMap<BotLevel, Integer>());
        /* creating new Counting strategy without random */
        CountingStrategy countingStrategy = new CountingStrategy() {
            @Override
            public int countPoints(Dice dice) {
                return 5;
            }

            @Override
            public int generateWinningNumber(int diceNumber) {
                return 6;
            }
        };
        GameStrategy gameStrategy = new NGameStrategy(settings, state, countingStrategy);
        RoomController roomController = new RoomController(null, settings, null, gameStrategy){
            @Override
            public void updateGameState(){}

            @Override
            public void handleEndOfTimeRequest(){}
        };
        gameStrategy.setRoomController(roomController);
        ((NGameStrategy)gameStrategy).setAfterRerollWaitTime(1);

        gameStrategy.startGame();
        currPlayer = state.getCurrentPlayer();
        int currPlayerIndex = newPlayers.indexOf(currPlayer);
        boolean[] chosenDice = new boolean[settings.getDiceNumber()];
        java.util.Arrays.fill(chosenDice, false);
        for(int i = 0; i<5*numberOfPlayers; i++){
            gameStrategy.reroll(chosenDice);
            currPlayerIndex++;
            if(currPlayerIndex >= numberOfPlayers) currPlayerIndex = 0;
            assertTrue("It should be " + currPlayerIndex + " player playing",
                    newPlayers.indexOf(state.getCurrentPlayer()) == currPlayerIndex);
        }
    }
}