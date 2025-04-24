package ManageData;

import Models.GoogleBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManageGoogleBookDataTest {

    private ManageGoogleBookData manageGoogleBookData;

    @BeforeEach
    void setUp() {
        manageGoogleBookData = ManageGoogleBookData.getInstance();
    }

    @Test
    void testSearchBook() {
        List<GoogleBook> books = manageGoogleBookData.searchBook("Java");

        assertNotNull(books, "The list of books should not be null");
        assertTrue(books.size() > 0, "The list of books should contain at least one book");

        GoogleBook book = books.get(0);
        assertNotNull(book.getId(), "Book ID should not be null");
        assertNotNull(book.getTitle(), "Book title should not be null");
        assertNotNull(book.getPublisher(), "Book publisher should not be null");
        assertNotNull(book.getDescription(), "Book description should not be null");
        assertNotNull(book.getLink(), "Book link should not be null");
    }
}
