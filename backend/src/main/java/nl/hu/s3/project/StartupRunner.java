package nl.hu.s3.project;

import jakarta.transaction.Transactional;
import nl.hu.s3.project.lobby.data.PlayerDAO;
import nl.hu.s3.project.lobby.data.PlayerRepository;
import nl.hu.s3.project.lobby.domain.Player;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class StartupRunner implements CommandLineRunner {

    private final PlayerRepository playerDAO;

    public StartupRunner(PlayerRepository playerDAO) {
        this.playerDAO = playerDAO;
    }

    @Override
    public void run(String... args) throws Exception {
        if(playerDAO.selectPlayers().isEmpty()){
            playerDAO.addPlayer(new Player("freezeybob", "Bob De Vries", "bob@example.com"));
            playerDAO.addPlayer(new Player("BobChunker", "Bob de Groot", "bobber-2009@example.com"));
            playerDAO.addPlayer(new Player("AliceDeVries", "Alice de Vries", "alice@example.com"));
            playerDAO.addPlayer(new Player("bubbahotep", "Bubba Hotep", "swingingmummy@example.com"));
        }
    }
}
