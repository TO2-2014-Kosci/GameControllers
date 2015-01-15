package to2.dice.controllers;

import java.util.Timer;
import java.util.TimerTask;


public class MoveTimer {

    private int time;
    private RoomController roomController;
    private Timer timer = new Timer();
    private EndOfTimeTask timerTask;

    private class EndOfTimeTask extends TimerTask {

        @Override
        public void run() {
            roomController.handleEndOfTimeRequest();
        }
    }

    public MoveTimer(int time, RoomController roomController) {
        this.time = time;
        this.roomController = roomController;
    }


    public void start() {
        timer.purge();
        timerTask = new EndOfTimeTask();
        timer.schedule(timerTask, time);
    }

    public boolean tryStop() {
        try {
            return timerTask.cancel();
        } catch (NullPointerException e) {
            return true;
        }
    }
}
