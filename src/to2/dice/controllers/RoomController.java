package to2.dice.controllers;

import to2.dice.ai.Bot;
import to2.dice.ai.BotFactory;
import to2.dice.game.*;

import java.util.*;

public class RoomController {

    private final GameSettings settings;
    private final GameState state;
    private GameStrategy gameStrategy;
    private final AbstractGameController gameController;
    private final BotsAgent botsAgent;
    private MoveTimer moveTimer;

    private final List<String> observers = new ArrayList<String>();
    private final int roomInactivityTime = 5000;

    public RoomController(AbstractGameController gameController, GameSettings settings, GameState state, GameStrategy gameStrategy) {
        this.gameController = gameController;
        this.settings = settings;
        this.state = state;
        this.gameStrategy = gameStrategy;
        this.botsAgent = new BotsAgent(gameController);
        this.moveTimer = new MoveTimer(settings.getTimeForMove()*1000, this);
    }

    public void addObserver(String observerName) {
        observers.add(observerName);
        gameController.sendNewGameState();
    }

    public void removeObserver(String observerName) {
        observers.remove(observerName);
        if (isRoomEmpty()) {
            //TODO waiting roomInactivityTime and interrupt when addObserver
            gameController.sendFinishGameSignal();
        }
    }

    public void addHumanPlayer(String playerName) {
        Player player = new Player(playerName, false, settings.getDiceNumber());
        addPlayer(player);
    }

    public void addBotPlayer(String botName, Bot bot) {
        Player botPlayer = new Player((botName), true, settings.getDiceNumber());
        botsAgent.registerBot(botPlayer, bot);
        addPlayer(botPlayer);
    }

    public void removePlayer(String playerName) {
        if (isGameStarted()) {
            gameStrategy.removePlayerWithName(playerName);
        } else {
            state.removePlayerWithName(playerName);
        }
        gameController.sendNewGameState();
    }

    public boolean handleRerollRequest(boolean[] chosenDice) {
        boolean notTooLate = moveTimer.tryStop();
        if (notTooLate) {
            gameStrategy.reroll(chosenDice);
            if (state.isGameStarted()) {
                moveTimer.start();
            }
            updateGameState();
            return true;
        } else {
            return false;
        }
    }

    public void handleEndOfTimeRequest() {
        gameStrategy.addPenaltyToPlayer(state.getCurrentPlayer());
    }

    public void createBots() {
        int botId = 0;

        for (Map.Entry<BotLevel, Integer> entry : settings.getBotsNumbers().entrySet()) {

            BotLevel botLevel = entry.getKey();
            int botsNumber = entry.getValue();

            for (int i = 0; i < botsNumber; i++) {
                Bot bot = BotFactory.createBot(settings.getGameType(), botLevel, settings.getTimeForMove());
                String botName = botLevel.toString() + "_Bot#" + botId++;
                addBotPlayer(botName, bot);
            }
        }
    }

    public void updateGameState() {
        gameController.sendNewGameState();
        botsAgent.processNewGameState(state);
    }

    public GameState getGameState() {
        return state;
    }


    public String getCurrentPlayerName() {
        return state.getCurrentPlayer().getName();
    }

    public boolean isObserverWithName(String name) {
        return observers.contains(name);
    }

    public boolean isPlayerWithName(String name) {
        return state.isPlayerWithName(name);
    }

    public boolean isGameStarted() {
        return (state.isGameStarted());
    }

    public boolean isRoomFull() {
        return (state.getPlayers().size() == settings.getMaxPlayers());
    }


    private boolean isRoomEmpty() {
        return (observers.isEmpty());
    }

    private boolean isGameStartConditionMet() {
        return (state.getPlayers().size() == settings.getMaxPlayers() && !state.isGameStarted());
    }

    private void addPlayer(Player player) {
        state.addPlayer(player);
        gameController.sendNewGameState();

        if (isGameStartConditionMet()) {
            gameStrategy.startGame();
            updateGameState();
        }
    }

}
