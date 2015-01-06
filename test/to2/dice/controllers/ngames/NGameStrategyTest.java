package to2.dice.controllers.ngames;

import org.junit.Before;
import org.junit.Test;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameStrategy;
import to2.dice.controllers.ngames.strategies.PlusCountingStrategy;
import to2.dice.game.*;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.*;

public class NGameStrategyTest {
    private Player[] players = {new Player("first", false, 5), new Player("second", false, 5)};
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

        assertTrue("Only first dice should be different", diceDiff);

        if(curr == 0) curr = 1;
        else curr = 0;
        assertTrue("It should be next player, after current, playing", state.getCurrentPlayer() == players[curr]);
    }

    @Test
    public void newRoundAndfinishGame() throws Exception{
        System.out.println("TESTING NGAMESTRATEGY ENDING GAME");

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
        gameStrategy.startGame();
        assertTrue("Game should be started", state.isGameStarted());

        /* Setting sure win number */
        state.setWinningNumber(5);

        boolean[] chosenDice = new boolean[settings.getDiceNumber()];
        java.util.Arrays.fill(chosenDice, false);
        gameStrategy.reroll(chosenDice);
        assertTrue("Game should still be active", state.isGameStarted());
        assertTrue("It should be second round", state.getCurrentRound() == 2);

        boolean isPoint = false;
        for(Player p: players)
            if(p.getScore() > 0){
                isPoint = true;
            }
        assertTrue("Player should get point", isPoint);

        /* Setting sure win number */
        state.setWinningNumber(5);
        gameStrategy.reroll(chosenDice);

        for(Player p: players)
            if(p.getScore() == 0){
                isPoint = false;
                break;
            }
        assertTrue("Both players should have point", isPoint);
        assertTrue("Game should end", !state.isGameStarted());
    }

}