package Models.Factories;

import Models.UrlModel;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class UrlModelFactory implements Function {
    @Override
    public Object apply(Object o) {
        Map map = (HashMap) o;

        String originalUrl = (String) map.get("originalUrl");
        String shortUrl = (String) map.get("shortUrl");
        long createdAt = (long) map.get("createdAt");
        long lifetime = (long) map.get("lifetime");
        int usages = (int) map.get("usages");
        int usagesLimit = (int) map.get("usagesLimit");
        UUID userId = UUID.fromString((String) map.get("userId"));
        UUID id = UUID.fromString((String) map.get("id"));

        UrlModel model = null;
        try {
            model = new UrlModel(userId, originalUrl, lifetime, usagesLimit, shortUrl, createdAt, usages);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        model.setId(id);

        return model;
    }
}