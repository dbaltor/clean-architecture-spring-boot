package library.domain;

import library.dto.Reader;
import library.dto.Book;
import library.domain.port.ReaderEntity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import static java.util.stream.Collectors.*;

import org.springframework.stereotype.Service;

@Service
public class ReaderEntityImpl implements ReaderEntity {

    private static final int MAXIMUM_ALLOWED_BORROWED_BOOKS = 6;

    /**
     * Validate whether the list of books can be borrowed by the reader.
     * @param reader        The reader trying to borrow the books
     * @param booksToBorrow The list of books to borrow
     * @return              The set of validation failures, is any. Otherwise, an empty set.         
     */
    public Set<BorrowingErrors> bookBorrowingValidator(Reader reader, List<Book> booksToBorrow) {
        // List of all validation criteria to be applied
        Map<BorrowingErrors, Predicate<List<Book>>> validators = new HashMap<>();
        
        // Validation criterium: maximum borrowing books not to exceed 
        Predicate<List<Book>> maxBorrowingExceeded = 
            books -> books.size() + reader.getBooks().size() > MAXIMUM_ALLOWED_BORROWED_BOOKS;
        validators.put(BorrowingErrors.MAX_BORROWED_BOOKS_EXCEEDED, maxBorrowingExceeded);
        // Future additional criteria
        // ...

        // Strategy pattern combining all criteria
        return validators.entrySet()
        .stream()
        .filter(map -> map.getValue().test(booksToBorrow)) // filter out the successful ones
        .map(map -> map.getKey())
        .collect(toSet());

        /*=======================================================
        Predicate<List<Book>> combinedValidator = validators
            .stream()
            .reduce(v -> true, Predicate::and);
        return combinedValidator.test(booksToBorrow); 
        =======================================================*/
    }
   
     /**
     * Validate whether the list of books can be returned by the reader.
     * @param reader        The reader trying to return the books
     * @param booksToBorrow The list of books to return
     * @return              The set of validation failures, if any. Otherwise, an empty set.        
     */
    public Set<ReturningErrors> bookReturningValidator(Reader reader, List<Book> booksToReturn) {
        return new HashSet<>();
    }
}