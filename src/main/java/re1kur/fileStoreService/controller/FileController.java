package re1kur.fileStoreService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import re1kur.fileStoreService.service.FileStoreService;

import java.io.IOException;


@RestController
@RequestMapping("api")
public class FileController {
    FileStoreService service;

    @Autowired
    public FileController(
            FileStoreService service) {
        this.service = service;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String fileName,
            @RequestParam("bucket") String bucket,
            @RequestParam("contentType") String contentType) {
        try {
            return service.upload(file.getBytes(), fileName, bucket, contentType);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @GetMapping("/{bucket}/{name}")
    public ResponseEntity<String> getFileUrl(
            @PathVariable("bucket") String bucket,
            @PathVariable("name") String fileName
    ) throws IOException {
        String url = service.getFileUrl(bucket, fileName);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url)
                .build();
    }

    @GetMapping("test")
    public ResponseEntity<String> hello(
    ) {
        return ResponseEntity.ok("Hello World.\nTest is successful.");
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }


}
