package libreria.libreria.uploadFile.service;

import libreria.libreria.item.model.Item;
import libreria.libreria.item.repository.ItemRepository;
import libreria.libreria.uploadFile.model.UploadFile;
import libreria.libreria.uploadFile.repository.UploadFileRepository;
import libreria.libreria.uploadFile.util.UploadFileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UploadFileService {

    private final UploadFileRepository uploadFileRepository;
    private final ItemRepository itemRepository;

    public String getSaveFileName(Long itemId) {
        UploadFile uploadFile = uploadFileRepository.findOneByItemId(itemId);

        return uploadFile.getSaveFileName();
    }

    @Transactional
    public void saveFile(MultipartFile uploadFile, Long itemId) throws IOException {
        Item item = itemRepository.findOneById(itemId);

        String saveFileName = makeSaveFileName(
                uploadFile.getOriginalFilename()
        );
        uploadFile.transferTo(new File(saveFileName));

        uploadFileRepository.save(UploadFileMapper.makeEntityForSave(
                saveFileName,
                item
        ));
    }

    @Transactional
    public void editFile(MultipartFile uploadFile, Long itemId) throws IOException {
        UploadFile file = uploadFileRepository.findOneByItemId(itemId);
        uploadFileRepository.deleteById(file.getId());
        String preFileName = file.getSaveFileName();
        File localFile = new File("C:\\Temp\\upload\\" + preFileName);
        if (localFile.delete()) {
            log.info("file : " + preFileName + " 삭제 완료");
        }

        String saveFileName = makeSaveFileName(
                uploadFile.getOriginalFilename()
        );
        uploadFile.transferTo(new File(saveFileName));

        uploadFileRepository.save(UploadFileMapper.makeEntityForEdit(
                file.getId(),
                saveFileName,
                file.getItem()
        ));
    }

    private String makeSaveFileName(String originalFileName) {
        UUID uuid = UUID.randomUUID();

        return uuid + "_" + originalFileName;
    }
}
