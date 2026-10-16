package nl.hu.s3.project.lobby.presentation.dto;

import nl.hu.s3.project.lobby.domain.Player;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record PlayerDTO(String username, String name, String email) {

    public void validate(){
        if(username == null || username.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is empty");
        }
        if(name == null || name.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is empty");
        }
        if(email == null || email.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is empty");
        }
    }


    public static PlayerDTO fromPlayer(Player player) {
        return new PlayerDTO(player.getUsername(), player.getFullName(), player.getEmailAddress());
    }
}
