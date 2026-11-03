package nl.hu.s3.project.lobby.presentation;

import jakarta.transaction.Transactional;
import nl.hu.s3.project.lobby.data.LobbyRepository;
import nl.hu.s3.project.lobby.data.PlayerDAO;
import nl.hu.s3.project.lobby.data.PlayerRepository;
import nl.hu.s3.project.lobby.domain.AvailableGame;
import nl.hu.s3.project.lobby.domain.Lobby;
import nl.hu.s3.project.lobby.domain.Player;
import nl.hu.s3.project.lobby.presentation.dto.LobbyDTO;
import nl.hu.s3.project.lobby.presentation.dto.NewLobbyDTO;
import nl.hu.s3.project.lobby.presentation.dto.PlayerDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Transactional
@RestController
@RequestMapping("/lobbies")
public class LobbyController {
    private final PlayerRepository players;
    private final LobbyRepository lobbies;

    public LobbyController(PlayerRepository players, LobbyRepository lobbies) {
        this.players = players;
        this.lobbies = lobbies;
    }

    @GetMapping
    public List<LobbyDTO> getLobbies() {
        List<LobbyDTO> dtos = new ArrayList<>();
        for (Lobby l : this.lobbies.findAll()) {
            dtos.add(LobbyDTO.fromLobby(l));
        }
        return dtos;
    }

    @GetMapping("/{id}")
    public LobbyDTO getLobby(@PathVariable long id) {
        try{
            Lobby found = this.lobbies.find(id);
            return LobbyDTO.fromLobby(found);
        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    }

    @PostMapping
    public ResponseEntity<LobbyDTO> createLobby(Authentication auth, @RequestBody NewLobbyDTO dto) {
        String currentUser = auth.getName();
        Player found = this.players.selectPlayer(currentUser);
        if (found == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Host not found");
        }
        try {
            AvailableGame game = AvailableGame.valueOf(dto.game());
            Lobby newLobby = new Lobby(found, game);
            this.lobbies.add(newLobby);
            return ResponseEntity.created(URI.create("/lobbies/" + newLobby.getId())).body(LobbyDTO.fromLobby(newLobby));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game not found");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLobby(Authentication auth, @PathVariable long id) {
        try{
            String currentUser = auth.getName();
            Lobby found = this.lobbies.find(id);
            if(!found.getHost().getUsername().equals(currentUser)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own lobbies");
            }

            this.lobbies.remove(found);
        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    }

    @PutMapping("/{id}")
    public LobbyDTO updateLobby(Authentication auth, @PathVariable long id, @RequestBody NewLobbyDTO dto) {
        String currentUser = auth.getName();
        try{
            Lobby foundLobby = this.lobbies.find(id);
            if(!foundLobby.getHost().getUsername().equals(currentUser)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own lobbies");
            }
            if(dto.game() != null){
                AvailableGame game = AvailableGame.valueOf(dto.game());
                foundLobby.updateGame(game);
            }

            return LobbyDTO.fromLobby(foundLobby);

        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game not found");
        }
    }

    @GetMapping("/{id}/players")
    public List<String> getPlayers(@PathVariable long id) {
        try{
            Lobby found = this.lobbies.find(id);
            return found.getPlayers().stream().map(p->p.getUsername()).toList();
        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<String> join(Authentication auth, @PathVariable long id) {
        try{
            String currentUser = auth.getName();
            Lobby found = this.lobbies.find(id);
            Player foundPlayer = this.players.selectPlayer(currentUser);
            if (foundPlayer == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player not found");
            }
            found.join(foundPlayer);

            return  ResponseEntity
                    .created(URI.create(String.format("/lobbies/%s/players/%s",
                            found.getId(),
                            foundPlayer.getUsername())))
                    .body(foundPlayer.getUsername());
        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    }

    @GetMapping("/{id}/players/{name}")
    public PlayerDTO getPlayer(Authentication auth, @PathVariable long id, @PathVariable String name) {
        try{
            String currentUser = auth.getName();
            if(!currentUser.equals(name)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can ask details about yourself");
            }

            Lobby found = this.lobbies.find(id);
            Player foundPlayer = null;
            for(Player p : found.getPlayers()){
                if(p.getUsername().equals(name)){
                    foundPlayer = p;
                }
            }
            if(foundPlayer == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
            }
            return PlayerDTO.fromPlayer(foundPlayer);
        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    }

    @DeleteMapping("/{id}/players/{name}")
    public void leave(Authentication auth, @PathVariable long id, @PathVariable String name) {
        try{
            String currentUser = auth.getName();
            if(!currentUser.equals(name)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only leave yourself");
            }
            Lobby found = this.lobbies.find(id);
            Player foundPlayer = null;
            for(Player p : found.getPlayers()){
                if(p.getUsername().equals(name)){
                    foundPlayer = p;
                }
            }
            if(foundPlayer == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found");
            }
            found.leave(foundPlayer);
        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    }
}
