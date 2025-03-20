package re1kur.fileStoreService.service.impl;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import re1kur.fileStoreService.service.FileStoreService;
//import software.amazon.awssdk.core.exception.SdkException;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class DefaultFileStoreService implements FileStoreService {
//    S3Client s3Client;
    MinioClient minioClient;
    private static final Logger logger = LoggerFactory.getLogger(DefaultFileStoreService.class);

    public DefaultFileStoreService(
//            S3Client s3Client,
            MinioClient minioClient) {
//        this.s3Client = s3Client;
        this.minioClient = minioClient;
    }

    @Override
    public void upload(MultipartFile file, String fileName, String bucket) throws IOException {
        long fileSize = file.getSize();
        if (fileSize <= 0) {
            throw new IOException("Invalid file size: " + fileSize);
        }
        String contentType = file.getContentType();
        logger.info(contentType);
        if (contentType == null || contentType.equals("application/octet-stream")) {
            contentType = "application/octet-stream"; // Установите тип по умолчанию
        }

        try (InputStream inputStream = file.getInputStream()) {
//            s3Client.putObject(
//                    PutObjectRequest.builder()
//                            .bucket(bucket)
//                            .key(fileName)
//                            .contentType(file.getContentType())
//                            .build(),
//                    RequestBody.fromInputStream(inputStream, fileSize)
//            );

            minioClient.putObject(PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .contentType(contentType)
                            .stream(inputStream, fileSize, -1)
                    .build());

            logger.info("File '{}' uploaded successfully to bucket '{}'", fileName, bucket);
//        } catch (S3Exception e) {
//            logger.error("Failed to upload file '{}' to bucket '{}': {}", fileName, bucket, e.getMessage());
//            throw new IOException("Failed to upload file to S3", e);
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
