package nl.hu.s3.project.lobby.presentation;

import nl.hu.s3.project.lobby.data.PlayerDAO;
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

@RestController
@RequestMapping("/lobbies")
public class LobbyController {
    private final PlayerDAO players;
    private List<Lobby> lobbies = new ArrayList<>();

    public LobbyController(PlayerDAO players) {
        this.players = players;
        this.lobbies.add(new Lobby(new Player("Host"), AvailableGame.BLACKJACK));
    }

    @GetMapping
    public List<LobbyDTO> getLobbies() {
        List<LobbyDTO> dtos = new ArrayList<>();
        for (Lobby l : this.lobbies) {
            dtos.add(LobbyDTO.fromLobby(l));
        }
        return dtos;
    }

    @GetMapping("/{id}")
    public LobbyDTO getLobby(@PathVariable int id) {
        try{
            Lobby found = this.lobbies.get(id);
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
            return ResponseEntity.created(URI.create("/lobbies/" + this.lobbies.indexOf(newLobby))).body(LobbyDTO.fromLobby(newLobby));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game not found");
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLobby(Authentication auth, @PathVariable int id) {
        try{
            String currentUser = auth.getName();
            Lobby found = this.lobbies.get(id);
            if(!found.getHost().getUsername().equals(currentUser)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own lobbies");
            }

            this.lobbies.remove(id);
        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    }

    @PutMapping("/{id}")
    public LobbyDTO updateLobby(Authentication auth, @PathVariable int id, @RequestBody NewLobbyDTO dto) {
        String currentUser = auth.getName();
        try{
            Lobby foundLobby = this.lobbies.get(id);
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
    public List<String> getPlayers(@PathVariable int id) {
        try{
            Lobby found = this.lobbies.get(id);
            return found.getPlayers().stream().map(p->p.getUsername()).toList();
        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    }

    @PostMapping("/{id}/players")
    public ResponseEntity<String> join(Authentication auth, @PathVariable int id) {
        try{
            String currentUser = auth.getName();
            Lobby found = this.lobbies.get(id);
            Player foundPlayer = this.players.selectPlayer(currentUser);
            if (foundPlayer == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player not found");
            }
            found.join(foundPlayer);

            return  ResponseEntity
                    .created(URI.create(String.format("/lobbies/%s/players/%s",
                            this.lobbies.indexOf(found),
                            foundPlayer.getUsername())))
                    .body(foundPlayer.getUsername());
        }catch(IndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found");
        }
    }

    @GetMapping("/{id}/players/{name}")
    public PlayerDTO getPlayer(Authentication auth, @PathVariable int id, @PathVariable String name) {
        try{
            String currentUser = auth.getName();
            if(!currentUser.equals(name)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can ask details about yourself");
            }

            Lobby found = this.lobbies.get(id);
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
    public void leave(Authentication auth, @PathVariable int id, @PathVariable String name) {
        try{
            String currentUser = auth.getName();
            if(!currentUser.equals(name)){
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only leave yourself");
            }
            Lobby found = this.lobbies.get(id);
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
