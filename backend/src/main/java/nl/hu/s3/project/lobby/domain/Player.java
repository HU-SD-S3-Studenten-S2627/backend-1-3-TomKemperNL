package nl.hu.s3.project.lobby.domain;

import java.util.Objects;

public class Player {
    private String login;
    private String fullName;
    private String emailAddress;

    protected Player(){}

    public Player(String username) {
        this.login = username;
    }

    public Player(String username, String fullName, String emailAddress) {
        this(username);
        this.fullName = fullName;
        this.emailAddress = emailAddress;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + login + '\'' +
                '}';
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player player)) return false;
        return Objects.equals(login, player.login);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(login);
    }

}
