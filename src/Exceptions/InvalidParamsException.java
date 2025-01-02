package Exceptions;

public class InvalidParamsException extends Exception{

    @Override
    public String toString() {
        return "Переданы неверные параметры";
    }
}
