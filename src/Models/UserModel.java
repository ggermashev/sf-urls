package Models;

import java.util.UUID;

public class UserModel extends Model {
    public UUID accessToken;

    public UserModel() {
        super();
        title = "User";
    }

    public boolean verify(String id) {
        return this.getId().equals(id);
    }


    @Override
    public String toString() {
        return "<Model/UserModel>" + "id=" + this.getId() + "</Model/UserModel>";
    }
}
