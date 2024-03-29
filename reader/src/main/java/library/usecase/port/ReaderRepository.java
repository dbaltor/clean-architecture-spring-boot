package library.usecase.port;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import library.dto.Reader;

public interface ReaderRepository {
    public Optional<Reader> findById(long id);
    public List<Reader> findByLastName(String lastName);
    public List<Reader> findByFirstName(String firstName);
    public List<Reader> findByDob(Date dob);
    public List<Reader> findAll();
    public List<Reader> findAll(int page, int size);
    public Reader save(Reader reader);
    public List<Reader> saveAll(List<Reader> readers);
    public void deleteAll();
    public void deleteAll(List<Reader> readers);
}