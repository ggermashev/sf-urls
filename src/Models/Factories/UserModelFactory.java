package Models.Factories;

import Models.UserModel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class UserModelFactory implements Function {
    @Override
    public Object apply(Object o) {
        Map map = (HashMap) o;
        String login = (String) map.get("login");
        String password = (String) map.get("password");
        UUID id = UUID.fromString((String) map.get("id"));

        var model = new UserModel(login, password);
        model.setId(id);

        return model;
    }
}