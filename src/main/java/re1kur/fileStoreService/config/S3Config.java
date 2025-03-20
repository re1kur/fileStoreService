package re1kur.fileStoreService.config;

import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//
//import java.net.URI;

@Configuration
public class S3Config {

    @Value("${minio.endpoint}")
    private String apiUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        Logger logger = LoggerFactory.getLogger(MinioClient.class);

        if (apiUrl == null || apiUrl.isEmpty()) {
            throw new IllegalArgumentException("API URL must not be null or empty");
        }
        if (accessKey == null || accessKey.isEmpty()) {
            throw new IllegalArgumentException("Access Key must not be null or empty");
        }
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Secret Key must not be null or empty");
        }

        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(apiUrl)
                    .credentials(accessKey, secretKey)
                    .build();

            logger.info("MinioClient successfully created for endpoint: {}", apiUrl);
            return client;
        } catch (Exception e) {
            logger.error("Failed to create MinioClient: {}", e.getMessage());
            throw new RuntimeException("Failed to create MinioClient", e);
        }

    }

//    @Bean
//    public S3Client s3Client() {
//        Logger logger = LoggerFactory.getLogger(S3Client.class);
//
//        // Проверка входных данных
//        if (apiUrl == null || apiUrl.isEmpty()) {
//            throw new IllegalArgumentException("API URL must not be null or empty");
//        }
//        if (accessKey == null || accessKey.isEmpty()) {
//            throw new IllegalArgumentException("Access Key must not be null or empty");
//        }
//        if (secretKey == null || secretKey.isEmpty()) {
//            throw new IllegalArgumentException("Secret Key must not be null or empty");
//        }
//
//        try {
//            S3Client client = S3Client.builder()
//                    .endpointOverride(URI.create(apiUrl))
//                    .credentialsProvider(StaticCredentialsProvider.create(
//                            AwsBasicCredentials.create(accessKey, secretKey)
//                    ))
//                    .region(Region.EU_WEST_1)
//                    .build();
//
//            logger.info("S3Client successfully created for endpoint: {}", apiUrl);
//            return client;
//        } catch (Exception e) {
//            logger.error("Failed to create S3Client: {}", e.getMessage());
//            throw new RuntimeException("Failed to create S3Client", e);
//        }
//    }
}
