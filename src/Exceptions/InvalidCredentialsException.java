package Exceptions;

public class InvalidCredentialsException extends Exception{

    @Override
    public String toString() {
        return "Введен несуществующий id.";
    }
}
