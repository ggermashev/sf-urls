package Exceptions;

public class TableNotFoundException extends Exception {

    @Override
    public String toString() {
        return "Таблица не найдена.";
    }
}
