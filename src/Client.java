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
                    case 4:
                        createShortUrlHandler();
                        break;
                    case 5:
                        navigateShortUrlHandler();
                        break;
                    case 6:
                        editShortUrlHandler();
                        break;
                    case 7:
                        removeShortUrlHandler();
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
        System.out.println("    4. Создать короткую ссылку");
        System.out.println("    5. Перейти по короткой ссылке");
        System.out.println("    6. Редактировать параметры короткой ссылки");
        System.out.println("    7. Удалить короткую ссылку");
        System.out.println("    0. Выход из приложения");
        System.out.println();
    }

    private static void createAccountHandler() {
        String id = null;
        try {
            id = (String) server.call("/user/create", null);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (id != null) {
                System.out.println("Аккаунт создан. Ваш id: " + id);
            } else {
                System.out.println("Не удалось создать аккаунт.");
            }
        }
    }

    private static void loginHandler() {
        System.out.println("Для входа в аккаунт ведите id");
        System.out.print("Ваш id: ");
        String id = scanner.next();

        Map params = new HashMap();
        params.put("id", id);
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

    private static void createShortUrlHandler() {
        System.out.println("Для создания короткой ссылки следуйте инструкциям");
        System.out.print("Url для сокращения: ");
        String url = scanner.next();
        System.out.println("\nДля ввода времени жизни короткой ссылки вы можете использовать следующие единицы измерения:");
        System.out.println("s, m, h, d - секунды, минуты, часы, дни соответственно.");
        System.out.println("Вы можете ввести несколько единиц измерения через пробел в формате <кол-во><единица измерения>.");
        System.out.println("Пример: 20m 15h");
        System.out.print("Время жизни: ");
        String lifetime = scanner.next();
        System.out.println("\nВведите лимит переходов по ссылке. Если число переходов неограниченно - введите 0");
        System.out.print("Лимит переходов: ");
        Integer usagesLimit = scanner.nextInt();

        Map params = new HashMap();
        params.put("accessToken", accessToken);
        params.put("url", url);
        params.put("lifetime", lifetime);
        params.put("usagesLimit", usagesLimit);

        String shortUrl = null;
        try {
            shortUrl = (String) server.call("/url/create", params);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (shortUrl != null) {
                System.out.println("Короткая ссылка для " + url + " создана: " + shortUrl);
            } else {
                System.out.println("Не удалось создать короткую ссылку.");
            }
        }
    }

    private static void navigateShortUrlHandler() {
        System.out.println("Введите сокращенную ссылку");
        System.out.print("url: ");
        String shortUrl = scanner.next();

        Map params = new HashMap();
        params.put("accessToken", accessToken);
        params.put("shortUrl", shortUrl);

        Boolean success = false;
        try {
            success = (Boolean) server.call("/url/navigate", params);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (!success) {
                System.out.println("Не удалось воспользоваться короткой ссылкой.");
            } else {
                System.out.println("Вы перешли по ссылке.");
            }
        }
    }

    private static void editShortUrlHandler() {
        System.out.println("Введите короткую ссылку и ее новые параметры.");
        System.out.print("url: ");
        String shortUrl = scanner.next();
        System.out.println("Введите новое время жизни короткой ссылки, если хотите обновить ее.");
        System.out.println("\nДля ввода времени жизни короткой ссылки вы можете использовать следующие единицы измерения:");
        System.out.println("s, m, h, d - секунды, минуты, часы, дни соответственно.");
        System.out.println("Вы можете ввести несколько единиц измерения через пробел в формате <кол-во><единица измерения>.");
        System.out.println("Пример: 20m 15h");
        System.out.println("Введите '-', если не хотите обновлять время жизни ссылки");
        System.out.print("Время жизни: ");
        String lifetime = scanner.next();

        System.out.println("\nВведите новый лимит переходов, если хотите обновить его.");
        System.out.println("Если число переходов неограниченно - введите 0");
        System.out.println("Введите '-', если не хотите обновлять лимит переходов");
        System.out.print("Лимит переходов: ");
        Object usagesLimit = scanner.next();

        if (Objects.equals(lifetime, "-")) {lifetime = null;}
        if (Objects.equals(usagesLimit, "-")) {
            usagesLimit = null;
        } else {
            usagesLimit = Integer.parseInt((String) usagesLimit);
        }

        Map params = new HashMap();
        params.put("accessToken", accessToken);
        params.put("shortUrl", shortUrl);
        params.put("lifetime", lifetime);
        params.put("usagesLimit", usagesLimit);

        Boolean success = false;
        try {
            success = (Boolean) server.call("/url/edit", params);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (success) {
                System.out.println("Ссылка успешно обновлена");
            } else {
                System.out.println("не удалось обновить ссылку");
            }
        }

    }

    private static void removeShortUrlHandler() {
        System.out.println("Введите короткую ссылку, которую хотите удалить");
        System.out.print("url: ");
        String shortUrl = scanner.next();

        Map params = new HashMap();
        params.put("accessToken", accessToken);
        params.put("shortUrl", shortUrl);

        Boolean success = false;
        try {
            success = (Boolean) server.call("/url/remove", params);
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (success) {
                System.out.println("Ссылка удалена");
            } else {
                System.out.println("Не удалось удалить ссылку");
            }
        }
    }
}
