package nl.hu.s3.project.lobby.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lobby {
    private int minPlayers;
    private int maxPlayers;
    private Player host;
    private List<Player> players;
    private GameState state;
    private AvailableGame game;

    public Lobby(Player host, AvailableGame game) {
        this.game = game;
        this.host = host;
        this.minPlayers = game.minPlayers();
        this.maxPlayers = game.maxPlayers();
        this.state = GameState.InLobby;
        this.players = new ArrayList<>();
        this.players.add(this.host);
    }

    private boolean enoughPlayers() {
        return this.minPlayers <= this.players.size() && this.players.size() <= this.maxPlayers;
    }

    public void join(Player p) {
        if (this.players.size() >= this.maxPlayers) {
            throw new RuntimeException("Max Players reached");
        }
        if (this.players.contains(p)) {
            throw new RuntimeException("Player already in game");
        }
        this.players.add(p);
    }

    public void leave(Player p) {
        this.players.remove(p);
    }

    public void start() {
        if (this.state != GameState.InLobby) {
            throw new RuntimeException("Cannot start, game already " + this.state);
        }
        if (!this.enoughPlayers()) {
            throw new RuntimeException(String.format("Not enough players, need %s-%s, got %s", this.minPlayers, this.maxPlayers, this.players.size()));
        }
        this.state = GameState.Started;
    }

    public GameState getState() {
        return this.state;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public AvailableGame getGame() {
        return game;
    }
}
