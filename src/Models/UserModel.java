package Models;

import java.util.UUID;

public class UserModel extends Model {
    public UUID accessToken;
    private String login;
    private String password;

    public UserModel(String login, String password) {
        super();
        title = "User";

        this.login = login;
        this.password = password;
    }

    public boolean verify(String login, String password) {
        return this.login.equals(login) && this.password.equals(password);
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "<Model/UserModel>" + "id=" + this.getId() + ";" + "login=" + this.login + ";" + "password=" + this.password + "</Model/UserModel>";
    }
}
