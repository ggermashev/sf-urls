package Controllers;

import Database.Database;
import Exceptions.*;
import utils.ITester;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ControllerTester implements ITester {
    UserController userController;
    UrlController urlController;

    public void test() {
        testUserController();
        testUrlController();
    }

    private void beforeTest() {
        Database database = new Database(false);
        userController = new UserController(database);
        urlController = new UrlController(database);
    }

    private void testUserController() {
        boolean good = true;

        good &= testCreateAccaunt();
        good &= testLogin();
        good &= testLogout();

        System.out.println(good ? "UserController tests passed" : "UserController tests failed");
    }

    private void testUrlController() {
        boolean good = true;

        good &= testDifferentUrls();
        good &= testUrlLimitExceeded();
        good &= testUrlLifetimeExpired();
        good &= testUrlInvalidLifetime();

        System.out.println(good ? "UrlController tests passed" : "UrlController tests failed");
    }

    private boolean testCreateAccaunt() {
        boolean good = true;

        good &= testCreateAccountPositive();

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

        try {
            String id = userController.createAccount();
            return id != null;
        } catch (Exception e) {
            System.out.println("testCreateAccountPositive failed");
            return false;
        }
    }

    private boolean testLoginPositive() {
        beforeTest();
        Map params = new HashMap();

        try {
            String id = userController.createAccount();
            params.put("id", id);
            UUID access =  userController.login(params);
            return access != null;
        } catch (Exception e) {
            System.out.println("testLoginPositive failed: " + e);
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

        try {
            userController.createAccount();

            params.put("id", "unknown");
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

        try {
            String id = userController.createAccount();
            params.put("id", id);
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

    private boolean testDifferentUrls() {
        beforeTest();
        Map params = new HashMap();

        try {
            String id = userController.createAccount();
            params.put("id", id);
            UUID accessToken = userController.login(params);
            params.put("accessToken", accessToken);
            params.put("url", "http://localhost");
            params.put("lifetime", "1m");
            params.put("usagesLimit", 1);
            params.put("mock", true);

            String shortUrl1 = urlController.createShortUrl(params);
            String shortUrl2 = urlController.createShortUrl(params);

            return !shortUrl1.equals(shortUrl2);
        } catch (Exception e) {
            System.out.println("Test different urls failed: " + e);
            return false;
        }
    }

    private boolean testUrlLimitExceeded() {
        beforeTest();
        Map params = new HashMap();
        params.put("mock", true);

        try {
            String id = userController.createAccount();
            params.put("id", id);
            UUID accessToken = userController.login(params);
            params.put("accessToken", accessToken);
            params.put("url", "http://localhost");
            params.put("lifetime", "5m");
            params.put("usagesLimit", 1);

            String shortUrl = urlController.createShortUrl(params);
            params.put("shortUrl", shortUrl);

            urlController.navigateUrl(params);
            urlController.navigateUrl(params);

            System.out.println("Test url limit exceeded failed!");
            return false;
        } catch (LinkUsageLimitExceededException e) {
            return true;
        } catch (Exception e) {
            System.out.println("Test url limit exceeded failed: " + e);
            return false;
        }
    }

    private boolean testUrlLifetimeExpired() {
        beforeTest();
        Map params = new HashMap();
        params.put("mock", true);

        try {
            String id = userController.createAccount();
            params.put("id", id);
            UUID accessToken = userController.login(params);
            params.put("accessToken", accessToken);
            params.put("url", "http://localhost");
            params.put("lifetime", "-1s");
            params.put("usagesLimit", 1);

            String shortUrl = urlController.createShortUrl(params);
            params.put("shortUrl", shortUrl);

            urlController.navigateUrl(params);

            System.out.println("Test url lifetime expired failed!");
            return false;
        } catch (LinkLifetimeExpiredException e) {
            return true;
        } catch (Exception e) {
            System.out.println("Test url lifetime expired failed: " + e);
            return false;
        }
    }

    private boolean testUrlInvalidLifetime() {
        beforeTest();
        Map params = new HashMap();
        params.put("mock", true);

        try {
            String id = userController.createAccount();
            params.put("id", id);
            UUID accessToken = userController.login(params);
            params.put("accessToken", accessToken);
            params.put("url", "http://localhost");
            params.put("lifetime", "qwe");
            params.put("usagesLimit", 1);

            urlController.createShortUrl(params);

            System.out.println("Test url invalid lifetime failed!");
            return false;
        } catch (InvalidParamsException e) {
            return true;
        } catch (Exception e) {
            System.out.println("Test url invalid lifetime failed: " + e);
            return false;
        }
    }
}
