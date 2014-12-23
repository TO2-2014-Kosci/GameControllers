package to2.dice.controllers.ngames;

import org.junit.Before;
import org.junit.Test;
import to2.dice.controllers.GameStrategy;
import to2.dice.controllers.ngames.strategies.PlusCountingStrategy;
import to2.dice.game.*;

import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class NGameStrategyTest {

    @Test
    public void testStartGame() throws Exception {
        System.out.println("TESTING GAME LOGIC IN NGAMESTRATEGY");
        NGameState state = new NGameState();
        Player[] players = new Player[2];
        players[0] = new Player("first", false, 5);
        players[1] = new Player("second", false, 5);
        state.addPlayer(players[0]);
        state.addPlayer(players[1]);

        GameSettings settings = new GameSettings(GameType.NPLUS, 5, "test", 2, 10, 2, 2, new HashMap<BotLevel, Integer>());
        CountingStrategy countingStrategy = new PlusCountingStrategy();
        GameStrategy gameStrategy = new NGameStrategy(settings, state, countingStrategy);

        assertTrue("Game shouldn't be started", !state.isGameStarted());
        assertTrue("Round shouldn't be started", state.getCurrentRound() == 0);
        gameStrategy.startGame();
        assertTrue("Game should be started", state.isGameStarted());
        assertTrue("It should be first round", state.getCurrentRound() == 1);

        assertTrue("It should be first player playing", state.getCurrentPlayer() == players[0]);

        int[] currDice = players[0].getDice().getDiceArray().clone();
        boolean[] chosenDice = new boolean[settings.getDiceNumber()];
        java.util.Arrays.fill(chosenDice, false);
        chosenDice[0] = true;
        gameStrategy.reroll(chosenDice);
        int[] newDice = players[0].getDice().getDiceArray();
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

        assertTrue("It should be second player playing", state.getCurrentPlayer() == players[1]);
    }
}