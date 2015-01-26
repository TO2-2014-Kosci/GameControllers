package to2.dice.controllers;

import to2.dice.ai.Bot;
import to2.dice.game.GameState;
import to2.dice.game.Player;
import to2.dice.messaging.RerollAction;

import java.util.*;
import java.util.concurrent.*;

public class BotsAgent {

    private final int minimalThinkingTime = 5000;
    private ExecutorService processor = Executors.newSingleThreadExecutor();
    private GameController gameController;
    private Map<Player, Bot> playerBotMap = new HashMap<Player, Bot>();
    private BlockingQueue<GameState> queue = new LinkedBlockingQueue<GameState>();

    private class GameStateProcessor implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    GameState state = queue.take();

                    Player currentPlayer = state.getCurrentPlayer();
                    if (state.isGameStarted() && state.getCurrentPlayer() != null && state.getCurrentPlayer().isBot()) {
                        Bot currentBot = playerBotMap.get(currentPlayer);
                        boolean[] chosenDice = currentBot.makeMove(currentPlayer.getDice().getDiceArray(),
                                getOtherDiceArrays(state, currentPlayer));
                        Thread.sleep(minimalThinkingTime);
                        gameController.handleGameAction(new RerollAction(currentPlayer.getName(), chosenDice));
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public BotsAgent(GameController gameController) {
        this.gameController = gameController;
        processor.submit(new GameStateProcessor());
    }

    public void registerBot(Player botPlayer, Bot bot) {
        playerBotMap.put(botPlayer, bot);
    }

    public void processNewGameState(GameState state) {
        try {
            queue.put(state);
        } catch (InterruptedException e) {
            return;
        }
    }

    public void shutdown() {
        processor.shutdownNow();
    }

    private List<int[]> getOtherDiceArrays(GameState state, Player player) {
        List<int[]> otherDice = new ArrayList<int[]>();
        for (Player p : state.getPlayers()) {
            if (p != player) {
                otherDice.add(p.getDice().getDiceArray());
            }
        }
        return otherDice;
    }
}
