package to2.dice.controllers;

public enum ControllerMessage {
    SENDER_IS_NOT_OBSERVER("Nie możesz usiąść do gry, której nie obserwujesz"),
    NO_SUCH_JOINED_OBSERVER("Nie możesz opuścić gry, której nie obserwujesz"),
    NO_SUCH_PLAYER("Nie ma takiego gracza"),
    OBSERVER_ALREADY_JOINED("Dołączyłeś już jako obserwator do tej gry"),
    PLAYER_ALREADY_SAT_DOWN("Usiadłeś już przy stole"),
    PLAYER_ALREADY_STAND_UP("Wstałeś już od stołu"),
    NO_EMPTY_PLACES("Nie ma już wolnych miejsc przy tym stole"),
    PLAYER_IS_IN_GAME("Nie można opuścić gry siedząc przy stole"),
    WRONG_DICE_NUMBER("Niepoprawna liczba kości"),
    OTHER_PLAYERS_TURN("Obecnie trwa tura innego gracza"),
    GAME_ALREADY_STARTED("Nie można usiąść do gry, która się już zaczęła"),
    GAME_IS_NOT_STARTED("Gra się jeszcze nie zaczęła");

    private final String text;

    private ControllerMessage(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
