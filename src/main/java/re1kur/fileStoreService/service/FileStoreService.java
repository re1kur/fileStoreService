package re1kur.fileStoreService.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileStoreService {
    void upload(byte[] fileBytes, String fileName, String bucket) throws IOException;

    InputStream download(String fileName, String bucket) throws IOException;

//    String getUrl(String fileName, String bucket);
}
