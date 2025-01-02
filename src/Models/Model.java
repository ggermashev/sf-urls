package Models;

import java.util.Map;
import java.util.UUID;

public abstract class Model {
    private UUID id;
    public String title;

    public Model() {
        id = UUID.randomUUID();
    }

    public String getId() {
        return id.toString();
    }

    public void setId(UUID id) {
        this.id = id;
    }
}

