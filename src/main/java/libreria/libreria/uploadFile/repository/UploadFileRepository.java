package libreria.libreria.uploadFile.repository;

import libreria.libreria.uploadFile.model.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    @Query("select u from UploadFile u join fetch u.item i where i.id = :id")
    UploadFile findOneByItemId(@Param("id") Long itemId);
}
