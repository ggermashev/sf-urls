package Exceptions;

public class NotLinkOwnerException extends Exception{

    @Override
    public String toString() {
        return "Вы не обладаете правами на управление этой короткой ссылки!";
    }
}
