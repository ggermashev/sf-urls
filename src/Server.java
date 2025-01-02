import Database.Database;
import Exceptions.*;
import Router.Router;

import java.util.Map;

public class Server implements AutoCloseable{
    Database db;
    Router router;

    public Server() {
        db = new Database();
        router = new Router(db);
    }

    public Object call(String route, Map params) throws InvalidCredentialsException, UnauthorizedException, InvalidParamsException, UnknownRouteException, EntityAlreadyExistsException {
        return router.call(route, params);
    }

    @Override
    public void close() throws Exception {
        db.close();
    }
}
