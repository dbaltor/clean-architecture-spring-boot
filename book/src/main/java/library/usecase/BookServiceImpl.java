package library.usecase;

import library.dto.Book;
import library.dto.Reader;
import library.usecase.port.BookRepository;
import library.usecase.port.BookService;
import library.usecase.exception.BorrowingException;
import library.usecase.exception.ReturningException;
import library.domain.port.ReaderEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.*;

import com.github.javafaker.Faker;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@Slf4j @RequiredArgsConstructor 
public class BookServiceImpl implements BookService{
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int DEFAULT_LOAD_SIZE = 100;
    private static final int DEFAULT_BORROWED_BOOKS = 2 * DEFAULT_PAGE_SIZE;

    private final @NonNull BookRepository bookRepository;
    private final @NonNull ReaderEntity readerEntity;

    private final Faker faker = new Faker();

    public List<Book> loadDatabase(Optional<Integer> nBooks, Optional<List<Reader>> readers) { 
        val NON_READERS = 3;
        val hasReaders = readers.isPresent() && readers.get().size() > 0;
        var nReaders = 0;
        if (hasReaders) {
             // Radndomly remove one reader who will not have any book assigned
            nReaders = readers.get().size();
            for (int i = 0; i < NON_READERS; i++){
                readers.get().remove((int)(Math.random() * nReaders));
                nReaders = readers.get().size();
            }
        }
        val total = nBooks.orElse(DEFAULT_LOAD_SIZE);
        val books = new ArrayList<Book>(total);   
        for(int i = 0; i < total; i++) {
            var book = Book.builder()
            .name(faker.book().title())
            .author(faker.book().author())
            .genre(faker.book().genre())
            .publisher(faker.book().publisher())
            .build();
            // Try to borrow each book to a differnt reader through all readers
            // or at least for DEFAULT_BORROWED_BOOKS books
            if (hasReaders && (i < nReaders || i < DEFAULT_BORROWED_BOOKS)) {
                try {
                    borrowBooks(
                        List.of(book), 
                        readers.get().get(i % nReaders)
                    );
                } catch(BorrowingException e) {
                    // book could not be borrowed but need to be saved
                    book = bookRepository.save(book);
                }
            }
            else { // save book without being borrowed
                book = bookRepository.save(book);
            }
            books.add(book);
        }
        log.info(String.format("Book database loaded with %d records.", total));
        return books;
    }

    public void cleanUpDatabase() {
        bookRepository.deleteAll();
        log.info("Book database cleaned up...");
    }

    public Optional<Book> retrieveBook(long id) {
        return bookRepository.findById(id);
    }

    public List<Book> retrieveBooks(Optional<Integer> pageNum, Optional<Integer> pageSize) {
        val books = new ArrayList<Book>();
        if (pageNum.isPresent()) {
            bookRepository
                .findAll(pageNum.get(), pageSize.orElse(DEFAULT_PAGE_SIZE))
                .forEach(books::add);
        }
        else {
            bookRepository
                .findAll()
                .forEach(books::add);
        }
        return books;
    }

    public List<Book> findBooksByIds(List<Long> ids) {
         return bookRepository.findByIds(ids);
     }    

    public List<Book> findBooksByReaderId(long id) {
        return bookRepository.findByReaderId(id);
    }

    public List<Book> borrowBooks(List<Book> booksToBorrow, Reader reader) throws BorrowingException {
        // filter out books already borrowed
        val books = booksToBorrow
            .stream()
            .filter(book -> book.getReaderId() == 0)
            .collect(toList());
        // Validate list as per business rules
        val errors = readerEntity.bookBorrowingValidator(reader, books);
        if (!errors.isEmpty()){
            throw new BorrowingException(errors);
        }
        var borrowedBooks = books.stream()
            .map(book -> {
                book.setReaderId(reader.getId()); // associate the book to the reader
                return book;
            })
            .collect(toList());
        bookRepository.saveAll(borrowedBooks);
        reader.getBooks().addAll(borrowedBooks);
        return borrowedBooks; // return list of borrowed books
   }

    public List<Book> returnBooks(List<Book> booksToReturn, Reader reader) throws ReturningException {
        // filter out books not borrowed by this reader
        val books = booksToReturn
            .stream()
            .filter(book -> book.getReaderId() == reader.getId())
            .collect(toList());
        // Validate list as per business rules
        val errors = readerEntity.bookReturningValidator(reader, books);
        if (!errors.isEmpty()){
            throw new ReturningException(errors);
        }
        // Validate list as per business rules
        val returnedBooks = books.stream()
            .map(book -> {
                book.setReaderId(0); // disassociate the book from the reader
                return book;
            })
            .collect(toList());
        bookRepository.saveAll(returnedBooks);
        reader.getBooks().removeAll(returnedBooks);
        return returnedBooks; // return list of returned books
    }
}