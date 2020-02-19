package emsbj;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

public interface BlobRepository extends CrudRepository<Blob, Long> {
    // by default spring reads an object from the database before deleting it including binary large objects
    // to prevent this "select" query the method is overridden to perform only the "delete" query
    @Override
    @Modifying
    @Transactional
    @Query("delete from Blob b where b.id = ?1")
    void deleteById(Long id);
}
