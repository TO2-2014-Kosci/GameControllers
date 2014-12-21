package to2.dice.controllers;

import to2.dice.game.*;
import to2.dice.messaging.GameAction;
import to2.dice.messaging.RerollAction;
import to2.dice.messaging.Response;
import to2.dice.server.GameServer;

public abstract class AbstractGameController implements GameController {
    protected final GameServer server;
    protected final GameSettings settings;
    private String creator;
    protected RoomController roomController;

    public AbstractGameController(GameServer server, GameSettings settings, String creator) {
        this.server = server;
        this.settings = settings;
        this.creator = creator;
    }

    public void initialize(GameState state, GameStrategy gameStrategy) {
        this.roomController = new RoomController(this, settings, state, gameStrategy);
        this.roomController.addObserver(creator);
        this.roomController.createBots();
    }

    public GameInfo getGameInfo() {
        return new GameInfo(settings, roomController.getGameState());
    }

    public synchronized Response handleGameAction(GameAction gameAction) {
        Response response = null;
        switch (gameAction.getType()) {
            case JOIN_ROOM:
                response = joinRoom(gameAction.getSender());
                break;
            case LEAVE_ROOM:
                response = leaveRoom(gameAction.getSender());
                break;
            case SIT_DOWN:
                response = sitDown(gameAction.getSender());
                break;
            case STAND_UP:
                response = standUp(gameAction.getSender());
                break;
            case REROLL:
                response = reroll(gameAction.getSender(), ((RerollAction) gameAction).getChosenDice());
                break;
        }
        return response;
    }

    public void sendNewGameState() {
        server.sendToAll(this, roomController.getGameState());
    }

    public void sendFinishGameSignal() {
        server.finishGame(this);
    }


    private Response joinRoom(String senderName) {
        if (roomController.isObserverWithName(senderName)) {
            return new Response(Response.Type.FAILURE, ControllerMessage.OBSERVER_ALREADY_JOINED.toString());
        } else {
            roomController.addObserver(senderName);
            return new Response(Response.Type.SUCCESS);
        }
    }

    private Response sitDown(String senderName) {
        if (roomController.isGameStarted()) {
            return new Response(Response.Type.FAILURE, ControllerMessage.GAME_ALREADY_STARTED.toString());
        } else {
            if (roomController.isRoomFull()) {
                return new Response(Response.Type.FAILURE, ControllerMessage.NO_EMPTY_PLACES.toString());
            } else if (!roomController.isObserverWithName(senderName)) {
                return new Response(Response.Type.FAILURE, ControllerMessage.SENDER_IS_NOT_OBSERVER.toString());
            } else if (roomController.isPlayerWithName(senderName)) {
                return new Response(Response.Type.FAILURE, ControllerMessage.PLAYER_ALREADY_SAT_DOWN.toString());
            } else {
                roomController.addHumanPlayer(senderName);
                return new Response(Response.Type.SUCCESS);
            }
        }
    }

    private Response standUp(String senderName) {
        if (!roomController.isObserverWithName(senderName)) {
            return new Response(Response.Type.FAILURE, ControllerMessage.SENDER_IS_NOT_OBSERVER.toString());
        } else if (!roomController.isPlayerWithName(senderName)) {
            return new Response(Response.Type.FAILURE, ControllerMessage.PLAYER_ALREADY_STAND_UP.toString());
        } else if (roomController.isGameStarted()) {
            return new Response(Response.Type.FAILURE, ControllerMessage.PLAYER_IS_IN_GAME.toString());
            //TODO allow users to standUp during game -> implement roomController.removePlayer() with safe removing currrentPlayer
        } else {
            roomController.removePlayer(senderName);
            return new Response(Response.Type.SUCCESS);
        }
    }

    private Response leaveRoom(String senderName) {
        if (!roomController.isObserverWithName(senderName)) {
            return new Response(Response.Type.FAILURE, ControllerMessage.NO_SUCH_JOINED_OBSERVER.toString());
        } else {
            roomController.removeObserver(senderName);
            return new Response(Response.Type.SUCCESS);
        }
    }

    private Response reroll(String senderName, boolean[] chosenDices) {
        if (!roomController.isGameStarted()) {
            return new Response(Response.Type.FAILURE, ControllerMessage.GAME_IS_NOT_STARTED.toString());
        } else if (!roomController.isPlayerWithName(senderName)) {
            return new Response(Response.Type.FAILURE, ControllerMessage.NO_SUCH_PLAYER.toString());
        } else if (chosenDices.length != settings.getDiceNumber()) {
            return new Response(Response.Type.FAILURE, ControllerMessage.WRONG_DICE_NUMBER.toString());
        } else if (!roomController.getCurrentPlayerName().equals(senderName)) {
            return new Response(Response.Type.FAILURE, ControllerMessage.OTHER_PLAYERS_TURN.toString());
        } else {
            boolean result = roomController.handleRerollRequest(chosenDices);
            if (result) {
                return new Response(Response.Type.SUCCESS);
            } else {
                return new Response(Response.Type.FAILURE, ControllerMessage.OTHER_PLAYERS_TURN.toString());
            }
        }
    }
}