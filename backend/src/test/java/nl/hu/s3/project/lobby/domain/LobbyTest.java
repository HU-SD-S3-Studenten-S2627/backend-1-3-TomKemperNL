package nl.hu.s3.project.lobby.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {

    @Test
    public void cantStartWithTooFewPlayers(){
        Lobby lobby = new Lobby(new Player("Host"), AvailableGame.Uno);
        assertThrows(RuntimeException.class, () -> {
           lobby.start();
        });
    }

    @Test
    public void cantJoinWithTooManyPlayers(){
        Lobby lobby = new Lobby(new Player("Host"), AvailableGame.Uno);
        for(int i=0; i< AvailableGame.Uno.maxPlayers() - 1; i++){
            lobby.join(new Player("P" + i+1));
        }
        assertThrows(RuntimeException.class, () -> {
            lobby.join(new Player("TooMany"));
        });
    }

    @Test
    public void cantDoubleJoin(){
        Lobby lobby = new Lobby(new Player("Host"), AvailableGame.Uno);

        lobby.join(new Player("P1"));

        assertThrows(RuntimeException.class, () -> {
            lobby.join(new Player("P1"));
        });
    }

    @Test
    public void canStartWithEnough(){
        Lobby lobby = new Lobby(new Player("Host"), AvailableGame.Uno);
        lobby.join(new Player("P1"));
        lobby.join(new Player("P2"));
        lobby.join(new Player("P3"));
        lobby.start();
        assertEquals(GameState.Started, lobby.getState());

    }

}