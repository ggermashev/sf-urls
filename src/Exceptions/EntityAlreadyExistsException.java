package Exceptions;

public class EntityAlreadyExistsException extends Exception{

    @Override
    public String toString() {
        return "Такой объект уже существует.";
    }
}
