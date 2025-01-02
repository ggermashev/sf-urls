package Exceptions;

public class UnknownRouteException extends Exception{

    @Override
    public String toString() {
        return "Вызвана несуществующая операция.";
    }
}
