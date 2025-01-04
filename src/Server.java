import Database.Database;
import Exceptions.*;
import Router.Router;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class Server implements AutoCloseable{
    Database db;
    Router router;

    public Server() {
        db = new Database();
        router = new Router(db);
    }

    public Object call(String route, Map params) throws InvalidCredentialsException, UnauthorizedException, InvalidParamsException, UnknownRouteException, EntityAlreadyExistsException, NotLinkOwnerException, URISyntaxException, IOException, EntityNotFoundException, LinkLifetimeExpiredException, LinkUsageLimitExceededException {
        return router.call(route, params);
    }

    @Override
    public void close() throws Exception {
        db.close();
    }
}
