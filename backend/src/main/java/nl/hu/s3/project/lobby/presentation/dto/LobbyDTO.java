package nl.hu.s3.project.lobby.presentation.dto;

import nl.hu.s3.project.lobby.domain.Lobby;

import java.util.List;

public record LobbyDTO(String game, String host, List<String> players) {
    public static LobbyDTO fromLobby(Lobby l) {
        return new LobbyDTO(
                l.getGame().toString(),
                l.getHost().getUsername(),
                l.getPlayers().stream().map(p -> p.getUsername()).toList());
    }
}
