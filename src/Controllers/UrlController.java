package Controllers;

import Database.Database;
import Exceptions.*;
import Models.UrlModel;
import Models.UserModel;
import utils.FileManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.UUID;


public class UrlController extends Controller {

    public UrlController(Database db) {super(db);}

    public String createShortUrl(Map params) throws UnauthorizedException, InvalidParamsException, URISyntaxException {
        UUID accessToken = (UUID) params.get("accessToken");
        String url = (String) params.get("url");
        String lifetime = (String) params.get("lifetime");
        Integer usagesLimit = (Integer) params.get("usagesLimit");

        Boolean mock = (Boolean) params.get("mock");
        if (mock == null) {mock = false;}

        if (usagesLimit < 0) {
            throw new InvalidParamsException();
        }

        if (accessToken == null) {
            throw new UnauthorizedException();
        }
        UserModel user = (UserModel) database.find("User", entity -> accessToken.equals(((UserModel) entity).accessToken));
        if (user == null) {
            throw new UnauthorizedException();
        }
        String userId = user.getId();


        Long timestamp = stringToTimestamp(lifetime);
        Long resTimestamp = timestamp;
        String envLifetime = FileManager.getEnv("MAX_URL_LIFETIME");
        if (!mock && envLifetime != null) {
            Long envTimestamp = stringToTimestamp(envLifetime);
            resTimestamp = Math.min(timestamp, envTimestamp);
            if (!resTimestamp.equals(timestamp)) {
                System.out.println("Максимальное время жизни не может превышать " + envLifetime + ". Установлено максимальное значение.");
            }
        }

        String envUsagesLimitStr = FileManager.getEnv("MIN_USAGES_LIMIT");
        Integer resUsagesLimit = usagesLimit;
        if (!mock && envUsagesLimitStr != null) {
            Integer envUsagesLimit = Integer.parseInt(envUsagesLimitStr);
            if (usagesLimit > 0) {
                resUsagesLimit = Math.max(usagesLimit, envUsagesLimit);
                if (!resUsagesLimit.equals(usagesLimit)) {
                    System.out.println("Лимит переходов по ссылке не может быть менее " + envUsagesLimitStr + ". Установлено минимальное значение.");
                }
            }
        }

        UrlModel urlModel = new UrlModel(userId, url, resTimestamp, resUsagesLimit);

        try {
            database.create(urlModel.title, urlModel);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return urlModel.getShortUrl();
    }

    public Boolean navigateUrl(Map params) throws UnauthorizedException, IOException, LinkLifetimeExpiredException, LinkUsageLimitExceededException, EntityNotFoundException {
        UUID accessToken = (UUID) params.get("accessToken");
        String shortUrl = (String) params.get("shortUrl");
        Boolean mock = (Boolean) params.get("mock");

        if (accessToken == null) {
            throw new UnauthorizedException();
        }
        UserModel user = (UserModel) database.find("User", entity -> accessToken.equals(((UserModel) entity).accessToken));
        if (user == null) {
            throw new UnauthorizedException();
        }

        UrlModel urlModel = (UrlModel) database.find("Url", entity -> ((UrlModel) entity).getShortUrl().equals(shortUrl));
        if (urlModel == null) {
            throw new EntityNotFoundException();
        }

        try {
            urlModel.navigate(mock);
            database.update(urlModel.title, urlModel);
        } catch (LinkLifetimeExpiredException e) {
            try {
                database.delete(urlModel.title, urlModel.getId());
            } catch (TableNotFoundException ex) {throw new RuntimeException(ex);}
            throw e;
        } catch (TableNotFoundException e) {
            throw new RuntimeException(e);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public Boolean removeUrl(Map params) throws UnauthorizedException, NotLinkOwnerException {
        UUID accessToken = (UUID) params.get("accessToken");
        String shortUrl = (String) params.get("shortUrl");

        if (accessToken == null) {
            throw new UnauthorizedException();
        }
        UserModel user = (UserModel) database.find("User", entity -> accessToken.equals(((UserModel) entity).accessToken));
        if (user == null) {
            throw new UnauthorizedException();
        }

        UrlModel urlModel = (UrlModel) database.find("Url", entity -> ((UrlModel) entity).getShortUrl().equals(shortUrl));
        if (!urlModel.checkOwner(user.getId())) {
            throw new NotLinkOwnerException();
        }

        try {
            database.delete(urlModel.title, urlModel.getId());
        } catch (TableNotFoundException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public Boolean editUrl(Map params) throws UnauthorizedException, NotLinkOwnerException, InvalidParamsException, EntityNotFoundException {
        UUID accessToken = (UUID) params.get("accessToken");
        String shortUrl = (String) params.get("shortUrl");
        String lifetime = (String) params.get("lifetime");
        Integer usagesLimit = (Integer) params.get("usagesLimit");

        Boolean mock = (Boolean) params.get("mock");
        if (mock == null) {mock = false;}

        if (accessToken == null) {
            throw new UnauthorizedException();
        }
        UserModel user = (UserModel) database.find("User", entity -> accessToken.equals(((UserModel) entity).accessToken));
        if (user == null) {
            throw new UnauthorizedException();
        }

        UrlModel urlModel = (UrlModel) database.find("Url", entity -> ((UrlModel) entity).getShortUrl().equals(shortUrl));
        if (urlModel == null) {
            throw new EntityNotFoundException();
        }
        if (!urlModel.checkOwner(user.getId())) {
            throw new NotLinkOwnerException();
        }

        if (lifetime != null) {
            Long timestamp = stringToTimestamp(lifetime);
            Long resTimestamp = timestamp;
            String envLifetime = FileManager.getEnv("MAX_URL_LIFETIME");
            if (!mock && envLifetime != null) {
                Long envTimestamp = stringToTimestamp(envLifetime);
                resTimestamp = Math.min(timestamp, envTimestamp);
                if (!resTimestamp.equals(timestamp)) {
                    System.out.println("Максимальное время жизни не может превышать " + envLifetime + ". Установлено максимальное значение.");
                }
            }

            urlModel.setLifetime(resTimestamp);
        }
        if (usagesLimit != null) {
            String envUsagesLimitStr = FileManager.getEnv("MIN_USAGES_LIMIT");
            Integer resUsagesLimit = usagesLimit;
            if (!mock && envUsagesLimitStr != null) {
                Integer envUsagesLimit = Integer.parseInt(envUsagesLimitStr);
                if (usagesLimit > 0) {
                    resUsagesLimit = Math.max(usagesLimit, envUsagesLimit);
                    if (!resUsagesLimit.equals(usagesLimit)) {
                        System.out.println("Лимит переходов по ссылке не может быть менее " + envUsagesLimitStr + ". Установлено минимальное значение.");
                    }
                }
            }
            urlModel.setUsagesLimit(resUsagesLimit);
        }

        try {
            database.update(urlModel.title, urlModel);
        } catch (TableNotFoundException | EntityNotFoundException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    private Long stringToTimestamp(String time) throws InvalidParamsException {
        try {
            String[] units = time.split(" ");
            long result = 0;

            for (String value: units) {
                Long amount = Long.parseLong(value.substring(0, value.length()-1));
                String unit = value.substring(value.length()-1);

                switch (unit) {
                    case "s":
                        amount *= 1000;
                        break;
                    case "m":
                        amount *= 60 * 1000;
                        break;
                    case "h":
                        amount *= 60 * 60 * 1000;
                        break;
                    case "d":
                        amount *= 24 * 60 * 60 * 1000;
                        break;
                    default:
                        throw new InvalidParamsException();
                }

                result += amount;
            }

            return result;
        } catch (NumberFormatException e) {
            throw new InvalidParamsException();
        }

    }
}
