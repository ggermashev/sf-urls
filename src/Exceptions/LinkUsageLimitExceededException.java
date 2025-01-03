package Exceptions;

public class LinkUsageLimitExceededException extends Exception{

    @Override
    public String toString() {
        return "Лимит переходов по этой ссылке исчерпан.";
    }
}
