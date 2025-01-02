package Exceptions;

public class UnauthorizedException extends Exception{

    @Override
    public String toString() {
        return "Вы не авторизованы.";
    }
}
