package nl.hu.s3.project.lobby.data;

import nl.hu.s3.project.lobby.domain.Player;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PlayerDAO {

    private DataSource dataSource;

    public PlayerDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Player> selectPlayers() {
        List<Player> foundPlayers = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT username, full_name, mail FROM players");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                String email = rs.getString("mail");
                foundPlayers.add(new Player(username, fullName, email));
            }
            return foundPlayers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertPlayer(Player player) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO players VALUES (?, ?, ?)");
            ps.setString(1, player.getUsername());
            ps.setString(2, player.getFullName());
            ps.setString(3, player.getEmailAddress());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updatePlayer(String username, Player updatedPlayer) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE players SET full_name = ?, mail = ? WHERE username = ?");
            ps.setString(1, updatedPlayer.getFullName());
            ps.setString(2, updatedPlayer.getEmailAddress());
            ps.setString(3, username);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletePlayer(Player found) {
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM players WHERE username = ?");
            ps.setString(1, found.getUsername());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Player selectPlayer(String username) {
        List<Player> foundPlayers = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT full_name, mail FROM players WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String fullName = rs.getString("full_name");
                String email = rs.getString("mail");
                foundPlayers.add(new Player(username, fullName, email));
            }
            if (foundPlayers.isEmpty()) {
                return null;
            } else if (foundPlayers.size() == 1) {
                return foundPlayers.get(0);
            } else {
                throw new RuntimeException("More than one player found for username " + username);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
