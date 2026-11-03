package nl.hu.s3.project.lobby.data;

import jakarta.persistence.EntityManager;
import nl.hu.s3.project.lobby.domain.Player;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlayerRepository {
    private EntityManager entities;
    public PlayerRepository(EntityManager entities) {
        this.entities = entities;
    }

    public List<Player> selectPlayers() {
        return this.entities.createQuery("SELECT p FROM Player p", Player.class).getResultList();
    }

    public Player selectPlayer(String username) {
        return this.entities.find(Player.class, username);
    }

    public void removePlayer(Player player) {
        this.entities.remove(player);
    }

    public Player addPlayer(Player player) {
        this.entities.persist(player);
        return player;
    }
}
