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
        UUID id = UUID.fromString((String) map.get("id"));

        var model = new UserModel();
        model.setId(id);

        return model;
    }
}