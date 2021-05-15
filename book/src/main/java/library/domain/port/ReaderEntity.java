package library.domain.port;

import library.dto.Reader;
import library.dto.Book;

import java.util.List;
import java.util.Set;

public interface ReaderEntity {
    public enum BorrowingErrors {
        MAX_BORROWED_BOOKS_EXCEEDED
        // Future error codes    
    }
    public enum ReturningErrors {
        PLACEHOLDER
        // Future error codes
     }

    public Set<BorrowingErrors> bookBorrowingValidator(Reader reader, List<Book> booksToBorrow);
    public Set<ReturningErrors> bookReturningValidator(Reader reader, List<Book> booksToReturn); 
}