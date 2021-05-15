package library.adapter.repository.sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import library.dto.Reader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

@Entity
@ToString @Getter @NoArgsConstructor @RequiredArgsConstructor(staticName = "of")
public class ReaderDb {
    private @Setter @GeneratedValue @Id long id;
    private @NonNull String firstName;
    private @NonNull String lastName;
    @Temporal(TemporalType.DATE)
    private @NonNull Date dob;
    private @NonNull String address;
    private @NonNull String phone;
    // DDD aggregate
    @ElementCollection(targetClass=BookDb.class)
    private @Setter List<BookDb> books = new ArrayList<>();

    public Reader reader() {
        return Reader.builder()
            .id(id)
            .firstName(firstName)
            .lastName(lastName)
            .dob(dob)
            .address(address)
            .phone(phone)
            .build();
    }

    public static ReaderDb of(Reader r) {
        val readerDb = ReaderDb.of(
            r.getFirstName(), 
            r.getLastName(), 
            r.getDob(), 
            r.getAddress(), 
            r.getPhone());
        readerDb.setId(r.getId());
        return readerDb;
    }
}