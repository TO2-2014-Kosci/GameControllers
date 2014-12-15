package to2.dice.controllers.common;

import to2.dice.ai.Bot;
import to2.dice.controllers.AbstractGameController;
import to2.dice.controllers.GameController;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.Player;
import to2.dice.server.GameServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomController {

    private GameThread gameThread;
    private final GameSettings settings;
    private final GameState state;
    private final int ROOM_INACTIVITY_TIME = 5000;
    private GameServer server;
    private GameController gameController;
    private List<String> observers = new ArrayList<String>();
    private Map<Player, Bot> bots;

    public RoomController(GameServer server, GameController gameController, GameSettings settings, GameState state, Map<Player, Bot> bots) {
        this.server = server;
        this.gameController = gameController;
        this.settings = settings;
        this.state = state;
        this.bots = bots;
    }

    public void setGameThread(GameThread gameThread) {
        this.gameThread = gameThread;
    }

    public void addObserver(String observerName) {
        observers.add(observerName);
    }

    public void removeObserver(String observerName) {
        observers.remove(observerName);
        if (isRoomEmpty()) {
            //TODO waiting some time and interrupt when addObserver
            server.finishGame(gameController);
        }
    }

    public synchronized void addPlayer(String playerName) {
        state.addPlayer(new Player(playerName, false, settings.getDiceNumber()));
        server.sendToAll(gameController, state);

        if (isGameStartConditionMet()) {
            gameThread.start();
        }
    }

    public synchronized void removePlayer(String playerName) {
        state.removePlayer(new Player(playerName, false, settings.getDiceNumber()));
        server.sendToAll(gameController, state);
    }

    public synchronized void addBot(String botName, Bot bot) {
        Player botPlayer = new Player((botName), true, settings.getDiceNumber());
        bots.put(botPlayer, bot);
        state.addPlayer(botPlayer);
    }

    public boolean isObserverWithName(String name) {
        return observers.contains(name);
    }

    public synchronized boolean isPlayerWithName(String name) {
        for (Player player : state.getPlayers()) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isRoomFull() {
        return (state.getPlayers().size() == settings.getMaxPlayers());
    }

    private boolean isRoomEmpty() {
        return (observers.isEmpty());
    }

    private synchronized boolean isGameStartConditionMet() {
        return (state.getPlayers().size() == settings.getMaxPlayers() && !state.isGameStarted());
    }

    public synchronized boolean isGameStarted() {
        return (state.isGameStarted());
    }
}
