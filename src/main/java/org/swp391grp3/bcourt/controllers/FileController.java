package org.swp391grp3.bcourt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.swp391grp3.bcourt.entities.FileData;
import org.swp391grp3.bcourt.repo.FileRepo;
import org.swp391grp3.bcourt.services.FileService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    @Autowired
    private FileRepo fileRepo;  // Your repository for file data
    @Autowired
    private FileService fileService;
    @GetMapping
    public List<FileData> getAllPhotos() {
        return fileRepo.findAll();  // Adjust this to fetch and return the necessary file data
    }
    @PostMapping("/upload/{userId}")
    public ResponseEntity<String> uploadFileUser(@PathVariable String userId,
                                             @RequestParam("file") MultipartFile file) {
        try {
            String fileId = fileService.uploadFileToFileSystem(file, userId);
            return ResponseEntity.ok().body(fileId); // Return fileId or any other response
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }
    @PostMapping("/upload/court/{courtId}")
    public ResponseEntity<String> uploadFileCourt(@PathVariable String courtId,
                                                 @RequestParam("file") MultipartFile file) {
        try {
            String fileId = fileService.uploadFileCourtToFileSystem(file, courtId);
            return ResponseEntity.ok().body(fileId); // Return fileId or any other response
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
        }
    }
    @GetMapping("{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileId)
        throws IOException {
        byte[] fileData = fileService.getFileFromFileSystem(fileId);
        return ResponseEntity.ok().contentType(MediaType.valueOf("image/png")).body(fileData);
    }
}
