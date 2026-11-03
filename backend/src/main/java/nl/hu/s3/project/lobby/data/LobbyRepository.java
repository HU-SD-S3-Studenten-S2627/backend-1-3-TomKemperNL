package nl.hu.s3.project.lobby.data;


import jakarta.persistence.EntityManager;
import jakarta.persistence.Lob;
import nl.hu.s3.project.lobby.domain.Lobby;
import nl.hu.s3.project.lobby.domain.Player;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LobbyRepository {
    private EntityManager entities;
    public LobbyRepository(EntityManager entities) {
        this.entities = entities;
    }

    public List<Lobby> findAll() {
        return this.entities.createQuery("SELECT p FROM Lobby p", Lobby.class).getResultList();
    }

    public Lobby find(Long id) {
        return this.entities.find(Lobby.class, id);
    }

    public void remove(Lobby lobby) {
        this.entities.remove(lobby);
    }

    public Lobby add(Lobby lobby) {
        this.entities.persist(lobby);
        return lobby;
    }
}