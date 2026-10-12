package nl.hu.s3.project.lobby.presentation;

import nl.hu.s3.project.lobby.data.PlayerDAO;
import nl.hu.s3.project.lobby.domain.Player;
import nl.hu.s3.project.lobby.presentation.dto.PlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        for (Player player : dao.selectPlayers()) {
            logger.debug("Found player: {}", player);
        }
        //TODO: Return nette DTOs van de spelers, in plaats van... whatever dit is
        throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "Not implemented yet");
    }
}
