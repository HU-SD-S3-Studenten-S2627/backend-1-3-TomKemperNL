package nl.hu.s3.project.lobby.presentation;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import nl.hu.s3.project.lobby.data.PlayerDAO;
import nl.hu.s3.project.lobby.domain.Player;
import nl.hu.s3.project.lobby.presentation.dto.LoginRequest;
import nl.hu.s3.project.lobby.presentation.dto.LoginResponse;
import nl.hu.s3.project.lobby.presentation.dto.PlayerDTO;
import nl.hu.s3.project.lobby.presentation.dto.UpdatePlayerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static nl.hu.s3.project.SecurityConfig.SECRET;

@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerDAO dao;

    public PlayerController(PlayerDAO playerDAO) {
        this.dao = playerDAO;
    }

    @GetMapping
    public List<PlayerDTO> getPlayers() {
        List<PlayerDTO> dtos = new ArrayList<>();
        for (Player player : dao.selectPlayers()) {
            dtos.add(PlayerDTO.fromPlayer(player));
        }
        return dtos;
    }

    @PostMapping
    public ResponseEntity<PlayerDTO> addPlayer(@RequestBody PlayerDTO newPlayer) {
        newPlayer.validate();
        this.dao.insertPlayer(new Player(newPlayer.username(), newPlayer.name(), newPlayer.email()));
        return ResponseEntity.created(URI.create("/players/" + newPlayer.username())).body(newPlayer);
    }

    @GetMapping("/{name}")
    public PlayerDTO getPlayer(@PathVariable("name") String name) {
        Player found = this.dao.selectPlayer(name);
        if (found == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return PlayerDTO.fromPlayer(found);
    }

    @PutMapping("/{name}")
    public PlayerDTO updatePlayer(@PathVariable("name") String name, @RequestBody UpdatePlayerDTO updatedPlayer) {
        Player found = this.dao.selectPlayer(name);
        if (found == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        updatedPlayer.validate();
        found.setEmailAddress(updatedPlayer.email());
        found.setFullName(updatedPlayer.name());
        return PlayerDTO.fromPlayer(found);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePlayer(@PathVariable("name") String name) {
        Player found = this.dao.selectPlayer(name);
        if (found == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        dao.deletePlayer(found);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req){
        Player found = this.dao.selectPlayer(req.username());
        if (found == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if(!req.password().equals(req.username() + "pwd")){ //Hier zou je normaal echt het wachtwoord checken
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        var builder = Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), Jwts.SIG.HS512)
                .issuer("HU-SD-S3")
                .expiration(Date.from(Instant.now().plus(1, DAYS)))
                .subject(found.getUsername());

        if(found.getUsername().equals("AliceDeVries")){ //En hier kun je iets complexers doen om een rol te bepalen...
            builder = builder.claim("role", "ADMIN");
        }

        return new LoginResponse(builder.compact());
    }
}
