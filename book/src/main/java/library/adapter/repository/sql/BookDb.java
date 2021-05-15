package library.adapter.repository.sql;

import library.dto.Book;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

@Entity
@ToString @Getter @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class BookDb {
    //@GeneratedValue(strategy = GenerationType.AUTO) // default approach equivalent to the below   
    private @Setter @Id @GeneratedValue long id;
    private @NonNull String name;
    private @NonNull String author;
    private @NonNull String genre;
    private @NonNull String publisher;
    // DDD aggregate id
    private @Setter long readerId;

    public Book book() {
        return Book.builder()
            .id(id)
            .name(name)
            .author(author) 
            .genre(genre)
            .publisher(publisher)
            .readerId(readerId)
            .build();
    }

    public static BookDb of(Book b) {
        val bookDb = BookDb.of(
            b.getName(),
            b.getAuthor(),
            b.getGenre(),
            b.getPublisher());
        bookDb.setId(b.getId());
        bookDb.setReaderId(b.getReaderId());    
        return bookDb;
    }
}