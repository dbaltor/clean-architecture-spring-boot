package library.adapter.repository.sql;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface SqlBookRepository extends PagingAndSortingRepository<BookDb, Long> {
    public List<BookDb> findByName(String name);

    @Query(value = "select b from BookDb b where b.readerId = :readerId")
    public List<BookDb> findByReader(@Param("readerId") long readerId);
}
