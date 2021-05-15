package library;

import library.dto.Book;
import library.dto.Reader;
import library.usecase.port.BookRepository;
import library.usecase.port.ReaderRepository;
import library.usecase.port.BookService;
import library.usecase.exception.BorrowingException;
import library.usecase.exception.ReturningException;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;

import com.github.javafaker.Faker;
import lombok.val;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LibraryApplication.class)
@AutoConfigureMockMvc
public class LibraryApplicationTest{

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private BookService bookService;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private ReaderRepository readerRepository;
	
	private final Faker faker = new Faker();	

	// List of books created during the test to be removed right after
	private List<Book> testBooks = new ArrayList<>();
	private static final int NUM_TEST_BOOKS = 20;
	// List of readers created during the tests to be removed right after
	private List<Reader> testReaders = new ArrayList<>();
	
    @After
    public void teardown() {
		// Delete all readers created for the tests
		readerRepository.deleteAll(testReaders);
		// Delete all books created for the tests
		bookRepository.deleteAll(testBooks);
    }	

	@Test
	public void contextLoads() {
	}

	@Test
	public void shouldLoadHomePage() throws Exception {
		mockMvc.perform(get("/"))
			.andDo(print())
			.andExpect(view().name("Index"));
	}

	@Test
	public void shouldFindById() {
		//Given
		testBooks = List.of(
			Book.builder().name("Java").author("").genre("").publisher("").build(),
			Book.builder().name("Node").author("").genre("").publisher("").build(),
			Book.builder().name("Go").author("").genre("").publisher("").build());
		// Added to testBooks to be removed durig the teardown
		testBooks = bookRepository.saveAll(testBooks);
		//When
		val book = bookService.retrieveBook(testBooks.get(1).getId());
		//Then
		assertTrue(book.isPresent());
	}

	@Test
	public void shouldFindByIds() {
		//Given
		val ids = Stream.iterate(0, e -> e + 1)
				.limit(NUM_TEST_BOOKS)
				.map(e -> Book.builder()
					.name(faker.book().title())
					.author(faker.book().author())
					.genre(faker.book().genre())
					.publisher(faker.book().publisher())
					.build())
				.map(book -> bookRepository.save(book))
				// Added to testBooks to be removed durig the teardown
				.peek(testBooks::add) 
				.map(book -> book.getId())
				.collect(toList());
		//When
		val books = bookService.findBooksByIds(ids);
		//Then
		assertThat(books.size(), is(NUM_TEST_BOOKS));
	}

	@Test
	public void shouldRetrieveAllBooks() {
		//Given
		// Added to testBooks to be removed durig the teardown
		testBooks = bookService.loadDatabase(Optional.of(NUM_TEST_BOOKS), Optional.empty());
		//When
		val books = bookService.retrieveBooks(Optional.empty(), Optional.empty());
		//Then
		assertThat(books.size(), is(NUM_TEST_BOOKS));
	}

	@Test
	public void shouldBorrowBooks() throws BorrowingException{
		// Given
		// Added to testReaders to be removed durig the teardown
		testReaders.add(readerRepository.save(
			Reader.builder()
				.firstName("John") 
				.lastName("Doe") 
				.dob(new Date())
				.address("")
				.phone("")
				.build()));
		testBooks = List.of(
			Book.builder().name("Java").author("").genre("").publisher("").build(),
			Book.builder().name("Node").author("").genre("").publisher("").build(),
			Book.builder().name("Go").author("").genre("").publisher("").build());
		// Added to testBooks to be removed durig the teardown
		testBooks = bookRepository.saveAll(testBooks);
		// When
		bookService.borrowBooks(testBooks, testReaders.get(0));
		// Then
		val borrowedBooks = bookService.findBooksByReaderId(testReaders.get(0).getId());
		assertThat(borrowedBooks.size(), is(testBooks.size()));
	}

	@Test
	public void shouldReturnBooks() throws BorrowingException, ReturningException{
		// Given
		// Added to testReaders to be removed durig the teardown
		testReaders.add(readerRepository.save(
			Reader.builder()
				.firstName("John") 
				.lastName("Doe") 
				.dob(new Date())
				.address("")
				.phone("")
				.build()));
		testBooks = List.of(
			Book.builder().name("Java").author("").genre("").publisher("").build(),
			Book.builder().name("Node").author("").genre("").publisher("").build(),
			Book.builder().name("Go").author("").genre("").publisher("").build());
		// Added to testBooks to be removed durig the teardown
		testBooks = bookRepository.saveAll(testBooks);
		bookService.borrowBooks(testBooks, testReaders.get(0));
		// When
		bookService.returnBooks(testBooks, testReaders.get(0));
		// Then
		val borrowedBooks = bookService.findBooksByReaderId(testReaders.get(0).getId());
		assertThat(borrowedBooks.size(), is(0));
	}
}
