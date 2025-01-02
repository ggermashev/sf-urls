package Exceptions;

public class EntityNotFoundException extends Exception{

    @Override
    public String toString() {
        return "Объект не найден.";
    }
}
