package Router;

import Controllers.UserController;
import Database.Database;
import Exceptions.*;

import java.util.Map;

public class Router {
    UserController userController;

    public Router(Database database) {
        userController = new UserController(database);
    }

    public Object call(String route, Map params) throws InvalidParamsException, UnknownRouteException, UnauthorizedException, InvalidCredentialsException, EntityAlreadyExistsException {
        switch (route) {
            case "/user/create":
                return userController.createAccount(params);
            case "/user/login":
                return userController.login(params);
            case "/user/logout":
                return userController.logout(params);

            default:
                throw new UnknownRouteException();
        }
    }
}
