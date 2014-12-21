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

    private final List<String> observers = new ArrayList<String>();

    private final int roomInactivityTime = 5000;
    private final int rerollsNumber = 2;
    private int currentRerollNumber;
    private ListIterator<Player> currentPlayerIt;
    private DiceRoller diceRoller;
//    private MoveTimer moveTimer;

    public RoomController(AbstractGameController gameController, GameSettings settings, GameState state, GameStrategy gameStrategy) {
        this.gameController = gameController;
        this.settings = settings;
        this.state = state;
        this.gameStrategy = gameStrategy;
        currentPlayerIt = state.getPlayers().listIterator();
        this.botsAgent = new BotsAgent(gameController);
        this.diceRoller = new DiceRoller(settings.getDiceNumber());
    }

    public void addObserver(String observerName) {
        observers.add(observerName);
        //Sending to all after joinRoom, because joining Player needs info
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
        state.removePlayer(new Player(playerName, false, settings.getDiceNumber()));
        currentPlayerIt = state.getPlayers().listIterator();
        gameController.sendNewGameState();
    }

    public boolean handleRerollRequest(boolean[] chosenDice) {
//        boolean notTooLate = moveTimer.tryStop();

//        if (notTooLate) {
            Dice currentPlayerDice = state.getCurrentPlayer().getDice();
            int[] diceArray = currentPlayerDice.getDiceArray();

            for (int i = 0; i < settings.getDiceNumber(); i++) {
                if (chosenDice[i]) {
                    diceArray[i] = diceRoller.rollSingleDice();
                }
            }
            currentPlayerDice.setDiceArray(diceArray);
            state.getCurrentPlayer().setDice(currentPlayerDice);

            nextPlayer();
            updateGameState();

            return true;
//        } else {
//            return false;
//        }
    }

    public void createBots() {
        int botId = 0;

        for (Map.Entry<BotLevel, Integer> entry : settings.getBotsNumbers().entrySet()) {

            BotLevel botLevel = entry.getKey();
            int botsNumber = entry.getValue();

            for (int i = 0; i < botsNumber; i++) {
                Bot bot = BotFactory.createBot(settings.getGameType(), botLevel, settings.getTimeForMove());
                String botName = "bot#" + botId++;
                addBotPlayer(botName, bot);
            }
        }
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

    public void updateGameState() {
        gameController.sendNewGameState();
        botsAgent.processNewGameState(state);
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
        currentPlayerIt = state.getPlayers().listIterator();
        gameController.sendNewGameState();

        if (isGameStartConditionMet()) {
            startGame();
        }
    }

    private void startGame() {
        state.setGameStarted(true);
        startNewRound();
        nextPlayer();
        updateGameState();
    }

    private void finishGame() {
        state.setGameStarted(false);
    }

    protected void startNewRound() {
        state.setCurrentRound(state.getCurrentRound() + 1);
        currentRerollNumber = 1;
        gameStrategy.rollInitialDice();
    }

    private void nextPlayer() {
        if (!currentPlayerIt.hasNext()) {
            if (currentRerollNumber < rerollsNumber) {
                /* it was not last reroll in this round, start new reroll */
                currentRerollNumber++;
                currentPlayerIt = state.getPlayers().listIterator();
            } else {
                /* it was last reroll in this round */
                Player roundWinner = gameStrategy.getRoundWinner();
                gameStrategy.addPointToPlayer(roundWinner);

                if (state.getCurrentRound() < settings.getRoundsToWin()) {
                    /* it was not last round*/
                    startNewRound();
                    currentPlayerIt = state.getPlayers().listIterator();
                } else {
                    /* it was last round */
                    finishGame();
                }
            }
        }
        if (state.isGameStarted()) {
            state.setCurrentPlayer(currentPlayerIt.next());
        }
    }
}
