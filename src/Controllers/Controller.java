package Controllers;

import Database.Database;

public abstract class Controller {
    Database database;

    Controller(Database database) {
        this.database = database;
    }
}
