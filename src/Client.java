import java.util.*;

public class Client {
    static Server server;
    static Scanner scanner = new Scanner(System.in);
    static UUID accessToken = null;

    public static void main(String[] args) {
        try (Server _server = new Server()) {
            server = _server;

            while (true) {
                Client.printMenu();

                int choice = -1;
                try {
                    choice = scanner.nextInt();
                } catch (Exception e) {
                    scanner.nextLine();
                }

                System.out.println();

                switch (choice) {
                    case 1:
                        createAccountHandler();
                        break;
                    case 2:
                        loginHandler();
                        break;
                    case 3:
                        logoutHandler();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Введен некорректный пункт меню");
                        break;
                }
            }
        } catch (Exception e) {}
    }

    private static void printMenu() {
        System.out.println("\nВыберите действие:");
        System.out.println("    1. Создать аккаунт");
        System.out.println("    2. Войти в аккаунт");
        System.out.println("    3. Выйти из аккаунта");
        System.out.println("    0. Выход из приложения");
        System.out.println();
    }

    private static void createAccountHandler() {
        System.out.println("Для создания аккаунта ведите логин и пароль");
        System.out.print("Ваш логин: ");
        String login = scanner.next();
        System.out.print("Ваш пароль: ");
        String password = scanner.next();

        Map params = new HashMap();
        params.put("login", login);
        params.put("password", password);
        Boolean success = false;
        try {
            success = (Boolean) server.call("/user/create", params);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (success) {
                System.out.println("Аккаунт создан.");
            } else {
                System.out.println("Не удалось создать аккаунт.");
            }
        }
    }

    private static void loginHandler() {
        System.out.println("Для входа в аккаунт ведите логин и пароль");
        System.out.print("Ваш логин: ");
        String login = scanner.next();
        System.out.print("Ваш пароль: ");
        String password = scanner.next();

        Map params = new HashMap();
        params.put("login", login);
        params.put("password", password);
        UUID token = null;
        try {
            token = (UUID) server.call("/user/login", params);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (token != null) {
                accessToken = token;
                System.out.println("Вы вошли в аккаунт.");
            } else {
                System.out.println("Не удалось войти в аккаунт.");
            }
        }
    }

    private static void logoutHandler() {
        Map params = new HashMap();
        params.put("accessToken", accessToken);
        Boolean success = false;
        try {
            success = (Boolean) server.call("/user/logout", params);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (success) {
                accessToken = null;
                System.out.println("Вы вышли из аккаунта.");
            } else {
                System.out.println("Не удалось выйти из аккаунта");
            }
        }
    }
}
