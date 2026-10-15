package nl.hu.s3.project.lobby.presentation;

import nl.hu.s3.project.lobby.data.PlayerDAO;
import nl.hu.s3.project.lobby.domain.Player;
import nl.hu.s3.project.lobby.presentation.dto.PlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerDAO dao;
    private final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    public PlayerController(PlayerDAO playerDAO) {
        this.dao = playerDAO;
    }

    @GetMapping
    public List<PlayerDTO> getPlayers() {
        List<PlayerDTO> output = new ArrayList<>();
        List<Player> players = dao.selectPlayers();
        for(Player player : players){
            output.add(new PlayerDTO(player.getLogin(), player.getFullName(), player.getEmailAddress()));
        }
        return output;
    }

    @GetMapping("/alternatief")
    public List<Player> getPlayers2() {
        return dao.selectPlayers();
    }
}
