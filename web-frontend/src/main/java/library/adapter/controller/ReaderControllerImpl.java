package library.adapter.controller;

import library.LibraryApplication;
import library.adapter.controller.port.ReaderController;
import library.dto.Reader;
import library.usecase.port.ReaderService;
import library.usecase.port.BookService;
import library.domain.port.ReaderEntity;
import library.usecase.exception.BorrowingException;
import library.usecase.exception.ReturningException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
public class ReaderControllerImpl implements ReaderController {
    public static final String READERS_MODEL_NAME = "readers";
    public static final String READER_MODEL_NAME = "readerId";
    public static final String READERS_TEMPLATE = "ReadersList";
    private final @NonNull ReaderService readerService;
    private final @NonNull BookService bookService;

    @Override
    public String listReaders(
        @RequestParam(name = "page") Optional<Integer> pageNum,
        @RequestParam(name = "size") Optional<Integer> pageSize,
        @RequestParam(name = "reader") Optional<Integer> readerId,
        Model model) {
            // Set background color of UI
            model.addAttribute(LibraryApplication.UI_CONFIG_NAME, LibraryApplication.getUIConfig());

            if (readerId.isPresent()) {
                model.addAttribute(READER_MODEL_NAME, readerId.get());
                //retrieve reader and add them to the Model object being returned to ViewResolver
                val readers = new ArrayList<Reader>();
                val reader = readerService.retrieveReader(readerId.get());
                if (reader.isPresent()) {
                    readers.add(reader.get());
                }
                // Adds readers to the Model object being returned to ViewResolve
                model.addAttribute(READERS_MODEL_NAME, readerId.get());
                model.addAttribute(READERS_MODEL_NAME, readers);
            }
            else {
                // Retrieve readers and add them to the Model object being returned to ViewResolver
                model.addAttribute(READERS_MODEL_NAME, readerService.retrieveReaders(pageNum, pageSize));
            }
            // Returns the name of the template view to reply this request
            return READERS_TEMPLATE;
    }

    @Override
    public String loadDatabase(@RequestParam Optional<Integer> count) {
        // load database
        val readers = readerService.loadDatabase(count);
        return String.format("Reader database loaded with %d records", readers.size());
    }

    @Override
    public String borrowBooks(
        @PathVariable(name = "id") long readerId,
        @RequestBody BooksRequest booksRequest) {
            val reader = readerService.retrieveReader(readerId);
            if (reader.isPresent()){
                val booksToBorrow = bookService.findBooksByIds(Arrays.asList(booksRequest.bookIds));
                /*&val booksToBorrow = Arrays.stream(booksRequest.bookIds)
                .mapToObj(id -> bookService.retrieveBook(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());*/
                try{ 
                    val borrowedBooks = bookService.borrowBooks(booksToBorrow, reader.get());
                    return String.format("The reader ID %d has borrowed %d book(s).", readerId, borrowedBooks.size());
                } catch(BorrowingException e) {
                    val errorMsg = new StringBuilder("Errors found:");
                    for(ReaderEntity.BorrowingErrors error : e.errors) {
                        switch (error) {
                            case MAX_BORROWED_BOOKS_EXCEEDED:
                                errorMsg.append(" *Maximum allowed borrowed books exceeded.");
                                break;
                            default:
                                errorMsg.append(" *Unexpected error.");
                        }
                    }
                    return errorMsg.toString();
                }
            }
            return String.format("No reader with ID %d has been found.", readerId);
    }

    @Override
    public String returnBooks(
        @PathVariable(name = "id") long readerId,
        @RequestBody BooksRequest booksRequest) {
            val reader = readerService.retrieveReader(readerId);
            if (reader.isPresent()){
                val booksToReturn = bookService.findBooksByIds(Arrays.asList(booksRequest.bookIds));
                /*val booksToReturn = Arrays.stream(booksRequest.bookIds)
                .mapToObj(id -> bookService.retrieveBook(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());*/
                try{ 
                    val returnedBooks = bookService.returnBooks(booksToReturn, reader.get());
                    return String.format("The reader ID %d has returned %d book(s).", readerId, returnedBooks.size());
                } catch(ReturningException e) {
                    val errorMsg = new StringBuilder("Errors found:");
                    for(ReaderEntity.ReturningErrors error : e.errors) {
                        switch (error) {
                            //Reserverd for future usage
                            //case PLACEHOLDER:
                            //    errorMsg.append(" *....");
                            //    break;
                            default:
                                errorMsg.append(" *Unexpected error.");
                        }
                    }
                    return errorMsg.toString();
                }
            }
            return String.format("No reader with ID %d has been found.", readerId);
    }
}