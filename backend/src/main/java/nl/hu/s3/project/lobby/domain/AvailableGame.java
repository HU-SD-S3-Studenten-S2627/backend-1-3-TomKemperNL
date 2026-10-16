package nl.hu.s3.project.lobby.domain;

public enum AvailableGame {
    BLACKJACK,
    UNO,
    THE_CREW,
    LOVE_LETTER,
    WEREWOLVES;
    
    public int minPlayers(){
        return switch (this){
            case BLACKJACK -> 1;
            case UNO, LOVE_LETTER, THE_CREW -> 3;
            case WEREWOLVES -> 8;
        };
    }

    public int maxPlayers() {
        return switch(this){
            case LOVE_LETTER -> 4;
            case THE_CREW -> 5;
            case BLACKJACK, UNO -> 6;
            case WEREWOLVES -> 25;
        };
    }
}
