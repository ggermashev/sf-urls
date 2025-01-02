package Controllers;

import Database.Database;
import Exceptions.*;
import Models.UserModel;

import java.util.Map;
import java.util.UUID;

public class UserController extends Controller {

    public UserController(Database database) {
        super(database);
    }

    public Boolean createAccount(Map params) throws InvalidParamsException, EntityAlreadyExistsException {
        String login = (String) params.get("login");
        String password = (String) params.get("password");

        if (login == null || password == null) {
            throw new InvalidParamsException();
        }

        UserModel user = new UserModel(login, password);
        try {
            UserModel existingAccount = (UserModel) database.find(user.title, entity -> ((UserModel) entity).getLogin().equals(login));
            if (existingAccount != null) {
                throw new EntityAlreadyExistsException();
            }

            database.create(user.title, user);
            return true;
        } catch (TableNotFoundException e) {
            System.out.println(e);
            return false;
        }
    }

    public UUID login(Map params) throws InvalidParamsException, InvalidCredentialsException {
        String login = (String) params.get("login");
        String password = (String) params.get("password");

        if (login == null || password == null) {
            throw new InvalidParamsException();
        }

        UUID accessToken = UUID.randomUUID();

        UserModel user = (UserModel) database.find("User", entity -> ((UserModel) entity).verify(login, password));
        if (user == null) {
            throw new InvalidCredentialsException();
        }
        user.accessToken = accessToken;

        try {
            database.update(user.title, user);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return accessToken;
    }

    public Boolean logout(Map params) throws UnauthorizedException {
        UUID accessToken = (UUID) params.get("accessToken");

        UserModel user = (UserModel) database.find("User", entity -> ((UserModel) entity).accessToken.equals(accessToken));
        if (user == null) {
            throw new UnauthorizedException();
        }
        user.accessToken = null;

        try {
            database.update(user.title, user);
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
