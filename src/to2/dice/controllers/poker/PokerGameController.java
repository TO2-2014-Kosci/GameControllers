package to2.dice.controllers.poker;

import to2.dice.controllers.AbstractGameController;
import to2.dice.controllers.GameController;
import to2.dice.controllers.GameControllerFactory;
import to2.dice.game.BotLevel;
import to2.dice.game.GameSettings;
import to2.dice.game.GameState;
import to2.dice.game.GameType;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.GameActionType;
import to2.dice.messaging.Response;
import to2.dice.server.GameServer;

import java.util.HashMap;

public class PokerGameController extends AbstractGameController {
    public PokerGameController(GameServer server, GameSettings settings, GameState state, String creator) {
        super(server, settings, creator);
        initialize(state, new PokerGameStrategy(settings, state));
    }

    public static void main(String[] args) {
        GameServer server = new GameServer() {
            @Override
            public void sendToAll(GameController sender, GameState state) {
            }
            @Override
            public void finishGame(GameController sender) {
            }
        };

        HashMap<BotLevel, Integer> botsNumber = new HashMap<BotLevel, Integer>();
        GameSettings settings = new GameSettings(GameType.POKER, 5, "Krakow", 2, 10, 2, 5, botsNumber);

        PokerGameController gameController = (PokerGameController) GameControllerFactory.createGameControler(server, settings, "LAKJFKLSDFJ");
        gameController.handleGameAction(new GameAction(GameActionType.JOIN_ROOM, "Player1"));
        gameController.handleGameAction(new GameAction(GameActionType.JOIN_ROOM, "Player2"));
        gameController.handleGameAction(new GameAction(GameActionType.SIT_DOWN, "Player1"));
        gameController.handleGameAction(new GameAction(GameActionType.SIT_DOWN, "Player2"));
//        sleep(10000);
        Response response = gameController.handleGameAction(new GameAction(GameActionType.SIT_DOWN, "Player3"));
        System.out.println(response.message);
    }
}
