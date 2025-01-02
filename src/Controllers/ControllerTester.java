package Controllers;

import Database.Database;
import Exceptions.InvalidCredentialsException;
import Exceptions.InvalidParamsException;
import Exceptions.UnauthorizedException;
import utils.ITester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ControllerTester implements ITester {
    UserController userController;

    public void test() {
        testUserController();
    }

    private void beforeTest() {
        Database database = new Database(false);
        userController = new UserController(database);
    }

    private void testUserController() {
        boolean good = true;

        good &= testCreateAccaunt();
        good &= testLogin();
        good &= testLogout();

        System.out.println(good ? "UserController tests passed" : "UserController tests failed");
    }

    private boolean testCreateAccaunt() {
        boolean good = true;

        good &= testCreateAccountPositive();
        good &= testCreateAccountInvalidParams();

        return good;
    }

    private boolean testLogin() {
        boolean good = true;

        good &= testLoginPositive();
        good &= testLoginInvalidParams();
        good &= testLoginInvalidCredentials();

        return good;
    }

    private boolean testLogout() {
        boolean good = true;

        good &= testLogoutPositive();
        good &= testLogoutUnauthorized();

        return good;
    }

    private boolean testCreateAccountPositive() {
        beforeTest();
        Map params = new HashMap();
        params.put("login", "login");
        params.put("password", "password");

        try {
            Boolean success = userController.createAccount(params);
            return success == true;
        } catch (Exception e) {
            System.out.println("testCreateAccountPositive failed");
            return false;
        }
    }

    private boolean testCreateAccountInvalidParams() {
        beforeTest();
        Map params = new HashMap();
        params.put("login", "login");

        try {
            userController.createAccount(params);
            System.out.println("testCreateAccountInvalidParams failed");
            return false;
        } catch (InvalidParamsException e) {
            return true;
        }
        catch (Exception e) {
            System.out.println("testCreateAccountInvalidParams failed");
            return false;
        }
    }

    private boolean testLoginPositive() {
        beforeTest();
        Map params = new HashMap();
        params.put("login", "login");
        params.put("password", "password");

        try {
            userController.createAccount(params);
            UUID access =  userController.login(params);
            return access != null;
        } catch (Exception e) {
            System.out.println("testLoginPositive failed");
            return false;
        }
    }

    private boolean testLoginInvalidParams() {
        beforeTest();
        Map params = new HashMap();
        params.put("login", "login");

        try {
            userController.login(params);
            System.out.println("testLoginInvalidParams failed");
            return false;
        } catch (InvalidParamsException e) {
            return true;
        }
        catch (Exception e) {
            System.out.println("testLoginInvalidParams failed");
            return false;
        }
    }

    private boolean testLoginInvalidCredentials() {
        beforeTest();
        Map params = new HashMap();
        params.put("login", "login");
        params.put("password", "password");

        try {
            userController.createAccount(params);

            params.put("password", "unknown");
            userController.login(params);
            System.out.println("testLoginInvalidCredentials failed");
            return false;
        } catch (InvalidCredentialsException e) {
            return true;
        }
        catch (Exception e) {
            System.out.println("testLoginInvalidCredentials failed");
            return false;
        }
    }

    private boolean testLogoutPositive() {
        beforeTest();
        Map params = new HashMap();
        params.put("login", "login");
        params.put("password", "password");

        try {
            userController.createAccount(params);
            UUID access = userController.login(params);

            params.put("accessToken", access);
            boolean res = userController.logout(params);
            return res;
        } catch (Exception e) {
            System.out.println("testLogoutPositive failed");
            return false;
        }
    }

    private boolean testLogoutUnauthorized() {
        beforeTest();
        Map params = new HashMap();

        try {
            params.put("accessToken", UUID.randomUUID());
            userController.logout(params);
            System.out.println("testLogoutUnauthorized failed");
            return false;
        } catch (UnauthorizedException e) {
            return true;
        }
        catch (Exception e) {
            System.out.println("testLogoutUnauthorized failed");
            return false;
        }

    }
}
