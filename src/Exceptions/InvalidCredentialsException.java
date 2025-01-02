package Exceptions;

public class InvalidCredentialsException extends Exception{

    @Override
    public String toString() {
        return "Введены неверные логин и пароль.";
    }
}
