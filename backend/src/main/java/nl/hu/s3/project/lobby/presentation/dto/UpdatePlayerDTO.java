package nl.hu.s3.project.lobby.presentation.dto;

import nl.hu.s3.project.lobby.domain.Player;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public record UpdatePlayerDTO(String name, String email) {
    public void validate(){

        if(name == null || name.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is empty");
        }
        if(email == null || email.isBlank()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is empty");
        }
    }
}
