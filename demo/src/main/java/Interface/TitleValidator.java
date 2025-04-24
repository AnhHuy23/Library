package Interface;

public interface TitleValidator {
    default public boolean isValidTitle(String bookName) {
        return bookName.matches("[a-zA-Z0-9\\s\\-:]{2,100}");
    }
}
