package library.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString @Getter @Builder
public class Book {
    private @Setter long id;
    private @NonNull String name;
    private @NonNull String author;
    private @NonNull String genre;
    private @NonNull String publisher;
    // DDD aggregate id
    private @Setter long readerId;
}