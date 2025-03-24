package re1kur.fileStoreService.service.impl;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import re1kur.fileStoreService.service.FileStoreService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class DefaultFileStoreService implements FileStoreService {
    MinioClient minioClient;
    private static final Logger log = LoggerFactory.getLogger(DefaultFileStoreService.class);

    public DefaultFileStoreService(
            MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public ResponseEntity<String> upload(byte[] fileBytes, String fileName, String bucket, String contentType) throws IOException {
        log.info("Received upload request - File: {}, Size: {} bytes, Bucket: {}, ContentType: {}",
                fileName, fileBytes.length, bucket, contentType);
        try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .contentType(contentType)
                    .stream(inputStream, fileBytes.length, -1)
                    .build());
            log.info("File '{}' uploaded successfully to bucket '{}' with ContentType '{}', size = {}.",
                    fileName, bucket, contentType, fileBytes.length);
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(fileName)
                    .build());
            log.info("File url received: {}", url);
            return ResponseEntity.ok(url);
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("AWS SDK error while uploading file '{}' with ContentType '{}' to bucket '{}': {}",
                    fileName, contentType, bucket, e.getMessage());
            throw new IOException(e);
        }
    }

    @Override
    public String getFileUrl(String bucket, String fileName) throws IOException {
        log.info("Received get url request - Bucket: {}, Filename: {}", bucket, fileName);
        try {
            String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(fileName)
                    .build());
            log.info("File url received: {}", url);
            return url;
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new IOException("Could not found file '%S' for creating url.".formatted(fileName), e);
        }
    }
}
