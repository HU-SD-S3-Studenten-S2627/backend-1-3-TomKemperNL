package nl.hu.s3.project.lobby.domain;

public enum AvailableGame {
    Blackjack,
    Uno,
    TheCrew,
    LoveLetter,
    Werewolves;
    
    public int minPlayers(){
        return switch (this){
            case Blackjack -> 1;
            case Uno, LoveLetter, TheCrew -> 3;
            case Werewolves -> 8;
        };
    }

    public int maxPlayers() {
        return switch(this){
            case LoveLetter -> 4;
            case TheCrew -> 5;
            case Blackjack, Uno -> 6;
            case Werewolves -> 25;
        };
    }
}
