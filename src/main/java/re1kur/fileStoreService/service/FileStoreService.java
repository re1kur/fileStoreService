package re1kur.fileStoreService.service;

import org.springframework.http.ResponseEntity;

import java.io.IOException;

public interface FileStoreService {
    ResponseEntity<String> upload(byte[] fileBytes, String fileName, String bucket, String contentType) throws IOException;


    String getFileUrl(String bucket, String fileName) throws IOException;

}
