package re1kur.fileStoreService.service.impl;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
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
    private static final Logger logger = LoggerFactory.getLogger(DefaultFileStoreService.class);

    public DefaultFileStoreService(
            MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public void upload(byte[] fileBytes, String fileName, String bucket) throws IOException {
        try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .stream(inputStream, fileBytes.length, -1)
                    .build());

            logger.info("File '{}' uploaded successfully to bucket '{}'", fileName, bucket);
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            logger.error("AWS SDK error while uploading file '{}' to bucket '{}': {}", fileName, bucket, e.getMessage());
            throw new IOException("AWS SDK error", e);
        }
    }

    @Override
    public InputStream download(String fileName, String bucket) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }
}
