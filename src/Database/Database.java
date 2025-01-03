package Database;

import Exceptions.EntityAlreadyExistsException;
import Exceptions.EntityNotFoundException;
import Exceptions.TableNotFoundException;
import Models.Factories.UrlModelFactory;
import Models.Factories.UserModelFactory;
import Models.Model;
import utils.FileManager;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class Database implements AutoCloseable {
    private final Map<String, Map<String, Model>> tables;
    private boolean withTablesStorage;

    public Database() {
        tables = new HashMap<>();

        tables.put("User", new HashMap<>());
        tables.put("Url", new HashMap<>());

        loadFromFile();
        withTablesStorage = true;
    }

    public Database(boolean withTablesStorage) {
        tables = new HashMap<>();

        tables.put("User", new HashMap<>());
        tables.put("Url", new HashMap<>());

        this.withTablesStorage = withTablesStorage;
        if (withTablesStorage) {
            loadFromFile();
        }
    }

    public Model create(String table, Model entity) throws TableNotFoundException, EntityAlreadyExistsException {
        if (!tables.containsKey(table)) {
            throw new TableNotFoundException();
        }
        if (tables.get(table).containsKey(entity.getId())) {
            throw new EntityAlreadyExistsException();
        }

        tables.get(table).put(entity.getId(), entity);
        return entity;
    }

    public Model delete (String table, String id) throws TableNotFoundException {
        if (!tables.containsKey(table)) {
            throw new TableNotFoundException();
        }

        return tables.get(table).remove(id);
    }

    public Model update(String table, Model entity) throws TableNotFoundException, EntityNotFoundException {
        if (!tables.containsKey(table)) {
            throw new TableNotFoundException();
        }
        if (!tables.get(table).containsKey(entity.getId())) {
            throw new EntityNotFoundException();
        }

        tables.get(table).put(entity.getId(), entity);
        return entity;
    }

    public Model find(String table, Function<Model, Boolean> isMatching) {
        var found = tables.get(table).values().stream().filter(isMatching::apply).findFirst();

        return found.orElse(null);
    }

    public int count(String table) throws TableNotFoundException {
        if (!tables.containsKey(table)) {
            throw new TableNotFoundException();
        }

        return tables.get(table).size();
    }

    private void loadToFile() throws TableNotFoundException, IOException {
        FileManager.loadTableToFile("User", tables.get("User"));
        FileManager.loadTableToFile("Url", tables.get("Url"));
    }

    private void loadFromFile() {
        Map constructorsMap = new HashMap();

        var userModelFactory = new UserModelFactory();
        constructorsMap.put("UserModel", userModelFactory);

        var urlModelFactory = new UrlModelFactory();
        constructorsMap.put("UrlModel", urlModelFactory);

        try {
            tables.put("User", FileManager.loadTableFromFile("User", constructorsMap));
        } catch (Exception e) {System.out.println(e);}
        try {
            tables.put("Url", FileManager.loadTableFromFile("Url", constructorsMap));
        } catch (Exception e) {System.out.println(e);}
    }

    @Override
    public void close() throws Exception {
        if (withTablesStorage) {
            loadToFile();
        }
    }
}
