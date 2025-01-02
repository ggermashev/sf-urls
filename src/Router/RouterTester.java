package Router;

import Database.Database;
import Exceptions.InvalidParamsException;
import Exceptions.UnknownRouteException;
import utils.ITester;

import java.util.HashMap;
import java.util.Map;

public class RouterTester implements ITester {
    Router router;

    public RouterTester() {
        Database database = new Database(false);
        router = new Router(database);
    }

    public void test() {
        boolean success = true;

        success &= testPathExists();
        success &= testPathNotExist();
        success &= testWrongData();

        System.out.println(success ? "Router tests passed" : "Router tests failed");
    }

    private boolean testPathExists() {
        Map params = new HashMap();
        params.put("login", "login");
        params.put("password", "password");
        try {
            router.call("/user/create",params);
            return true;
        } catch (Exception e) {
            System.out.println("Router test path exists failed");
            return false;
        }
    }

    private boolean testPathNotExist() {
        Map params = new HashMap();
        params.put("login", "login");
        params.put("password", "password");
        try {
            router.call("unknown",params);

            System.out.println("Router test path not exists failed");
            return false;
        } catch (UnknownRouteException e) {
            return true;
        } catch (Exception e) {
            System.out.println("Router test path not exists failed");
            return false;
        }
    }

    private boolean testWrongData() {
        Map params = new HashMap();
        params.put("login", "login");
        try {
            router.call("/user/create",params);

            System.out.println("Router test path not exists failed");
            return false;
        } catch (InvalidParamsException e) {
            return true;
        } catch (Exception e) {
            System.out.println("Router test path not exists failed");
            return false;
        }
    }
}
