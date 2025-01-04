package Router;

import Controllers.UrlController;
import Controllers.UserController;
import Database.Database;
import Exceptions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class Router {
    UserController userController;
    UrlController urlController;

    public Router(Database database) {
        userController = new UserController(database);
        urlController = new UrlController(database);
    }

    public Object call(String route, Map params) throws InvalidParamsException, UnknownRouteException, UnauthorizedException, InvalidCredentialsException, EntityAlreadyExistsException, URISyntaxException, IOException, EntityNotFoundException, LinkLifetimeExpiredException, LinkUsageLimitExceededException, NotLinkOwnerException {
        switch (route) {
            case "/user/create":
                return userController.createAccount();
            case "/user/login":
                return userController.login(params);
            case "/user/logout":
                return userController.logout(params);

            case "/url/create":
                return urlController.createShortUrl(params);
            case "/url/navigate":
                return urlController.navigateUrl(params);
            case "/url/edit":
                return urlController.editUrl(params);
            case "/url/remove":
                return urlController.removeUrl(params);

            default:
                throw new UnknownRouteException();
        }
    }
}
