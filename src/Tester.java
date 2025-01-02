import Controllers.ControllerTester;
import Router.RouterTester;

public class Tester {
    public static void main(String[] args) {
        RouterTester routerTester = new RouterTester();
        ControllerTester controllerTester = new ControllerTester();

        routerTester.test();
        controllerTester.test();
    }
}
