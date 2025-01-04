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

    public String createAccount() throws EntityAlreadyExistsException {
        UserModel user = new UserModel();
        try {
            user = (UserModel) database.create(user.title, user);
            return user.getId();
        } catch (TableNotFoundException e) {
            System.out.println(e);
            return null;
        }
    }

    public UUID login(Map params) throws InvalidParamsException, InvalidCredentialsException {
        String id = (String) params.get("id");

        if (id == null) {
            throw new InvalidParamsException();
        }

        UUID accessToken = UUID.randomUUID();

        UserModel user = (UserModel) database.find("User", entity -> ((UserModel) entity).verify(id));
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

        if (accessToken == null) {
            throw new UnauthorizedException();
        }
        UserModel user = (UserModel) database.find("User", entity -> accessToken.equals(((UserModel) entity).accessToken));
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
