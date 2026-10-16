package nl.hu.s3.project;

import nl.hu.s3.project.lobby.data.PlayerDAO;
import nl.hu.s3.project.lobby.domain.Player;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final PlayerDAO playerDAO;

    public StartupRunner(PlayerDAO playerDAO) {
        this.playerDAO = playerDAO;
    }

    @Override
    public void run(String... args) throws Exception {
        if(playerDAO.selectPlayers().isEmpty()){
            playerDAO.insertPlayer(new Player("freezeybob", "Bob De Vries", "bob@example.com"));
            playerDAO.insertPlayer(new Player("BobChunker", "Bob de Groot", "bobber-2009@example.com"));
            playerDAO.insertPlayer(new Player("AliceDeVries", "Alice de Vries", "alice@example.com"));
            playerDAO.insertPlayer(new Player("bubbahotep", "Bubba Hotep", "swingingmummy@example.com"));
        }
    }
}
