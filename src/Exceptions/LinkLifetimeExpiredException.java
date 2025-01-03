package Exceptions;

public class LinkLifetimeExpiredException extends Exception{

    @Override
    public String toString() {
        return "Время жизни ссылки истекло. Она будет удалена.";
    }
}
