package re1kur.fileStoreService.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re1kur.fileStoreService.service.FileStoreService;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("api")
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    FileStoreService service;

    @Autowired
    public FileController(
            FileStoreService service) {
        this.service = service;
    }

    @PostMapping("upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String fileName,
            @RequestParam("bucket") String bucket) throws IOException {
        service.upload(file, fileName, bucket);
        return ResponseEntity.ok().body("Successfully uploaded");
    }

    @GetMapping("download")
    public ResponseEntity<byte[]> download(
            @RequestParam("bucket") String bucket,
            @RequestParam("name") String fileName
    ) throws IOException {
        log.info("params: {}, {}", bucket, fileName);
        InputStream fileStream = service.download(fileName, bucket); // url: bucket/fileName
        byte[] bytes = fileStream.readAllBytes();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(bytes);
    }

    @GetMapping("hello")
    public ResponseEntity<String> hello(
    ) {
       return ResponseEntity.ok("Hello World");
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
