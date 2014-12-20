package to2.dice.controllers;

import to2.dice.ai.Bot;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;
import to2.dice.server.GameServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class GameThread {

    protected final GameController gameController;
    protected final GameServer server;
    protected final GameSettings settings;
    protected final GameState state;
    protected final DiceRoller diceRoller;
    protected RoomController roomController;
    protected Runnable gameRoutine;
    protected Map<Player, Bot> bots;
    protected Map<Player, Integer> numberOfAbsences = new HashMap<Player, Integer>();
    protected boolean[] chosenDice;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public GameThread(GameServer server, GameController gameController, GameSettings settings, GameState state, Map<Player, Bot> bots) {
        this.gameController = gameController;
        this.server = server;
        this.settings = settings;
        this.state = state;
        this.bots = bots;
        diceRoller = new DiceRoller(settings.getDiceNumber());
    }

    public void setRoomController(RoomController roomController) {
        this.roomController = roomController;
    }

    public void setGameRoutine(Runnable gameRoutine) {
        this.gameRoutine = gameRoutine;
    }

    public void start() {
            executor.execute(gameRoutine);
    }

    public void interrupt() {
        executor.shutdownNow();
    }

    public void handleRerollRequest(boolean[] chosenDice) {
        synchronized (gameController) {
            this.chosenDice = chosenDice;
            gameController.notify();
        }
    }

    public String getCurrentPlayerName() {
        synchronized (gameController) {
            return state.getCurrentPlayer().getName();
        }
    }


    protected void startNewRound() {
            state.setCurrentRound(state.getCurrentRound() + 1);
        }

    protected void rollInitialDice() {
        for (Player player : state.getPlayers()) {
            player.setDice(diceRoller.rollDice());
        }
    }

    protected List<int[]> getOtherDiceArrays(Player player) {
        List<int[]> otherDice = new ArrayList<int[]>();
        for (Player p : state.getPlayers()) {
            if (p != player) {
                otherDice.add(p.getDice().getDiceArray());
            }
        }
        return otherDice;
    }

    protected void addPointToPlayer(Player player) {
        player.setScore(player.getScore() + 1);
    }

    protected void addPenaltyToPlayer(Player player) {
        int currentAbsences = numberOfAbsences.get(player);
        currentAbsences++;
        if (currentAbsences == settings.getMaxInactiveTurns()) {
            removePlayer(player.getName());
        } else {
            numberOfAbsences.put(player, currentAbsences);
        }
    }

    protected void removePlayer(String senderName) {

    }

}