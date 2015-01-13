package to2.dice.controllers;

import org.junit.Before;
import org.junit.Test;
import to2.dice.controllers.ngames.NGameController;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.GameType;
import to2.dice.server.GameServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class MoveTimerTest {
    private RoomController roomController;
    private int penalty = 0;
    private class MoveTimer2 extends MoveTimer{
        private boolean started;
        public MoveTimer2(RoomController roomController){
            super(1000, roomController);
            started = false;
        }

        @Override
        public void start(){
            super.start();
            started = true;
        }

        public boolean isStarted(){ return started; }

        @Override
        public boolean tryStop() {
            started = false;
            return super.tryStop();
        }
    }
    private MoveTimer2 moveTimer;

    @Before
    public void setUp() throws Exception {
        roomController = new RoomController(null, new GameSettings(GameType.NPLUS, 5, "test", 5, 5, 5, 5,
                new HashMap<BotLevel, Integer>()), null, null){
            @Override
            public void handleEndOfTimeRequest() { penalty++; }

            @Override
            public boolean handleRerollRequest(boolean[] chosenDice) {
                boolean notTooLate = moveTimer.tryStop();
                if(notTooLate) return true;
                else return false;
            }
        };
        moveTimer = new MoveTimer2(roomController);
    }

    @Test
    public void testMoveTimer() throws Exception {
        moveTimer.start();
        assertTrue("moveTimer should be started", moveTimer.isStarted());
        boolean[] chosenDice = new boolean[5];
        java.util.Arrays.fill(chosenDice, false);
        assertTrue("Rerolling should be still possible", roomController.handleRerollRequest(chosenDice));

        moveTimer.start();
        assertTrue("moveTimer should be started", moveTimer.isStarted());
        Thread.sleep(1100);
        assertTrue("It should be TimeOut. Rerolling not possible", !roomController.handleRerollRequest(chosenDice));
        assertTrue("There should be one penalty point", penalty>0);
    }
}