package org.swp391grp3.bcourt.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.swp391grp3.bcourt.entities.FileData;
import org.swp391grp3.bcourt.repo.FileRepo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepo fileRepo;
    private final UserService userService;
    private final String path = "C:\\Users\\patho\\OneDrive\\Desktop\\bcourt\\bcourt\\src\\uploads";

    public String uploadFileToFileSystem(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Generate a unique file name
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + extension;

        // Construct the full file path
        String filePath = path + File.separator + fileName;

        try {
            // Save the file to the file system
            file.transferTo(new File(filePath));

            // Save file metadata to database
            FileData savedFileData = fileRepo.save(FileData.builder()
                    .fileName(originalFilename)
                    .fileUrl(filePath) // Store the absolute file path
                    .fileType(file.getContentType())
                    .build());
            userService.updateUserImg(userId, savedFileData);
            return savedFileData.getFileId();
        } catch (IOException e) {
            log.error("Failed to store file {}", originalFilename, e);
            throw e; // Rethrow the exception or handle accordingly
        }
    }
    public byte[] getFileFromFileSystem(String fileId)
            throws IOException
    {
        Optional<FileData> fileData = fileRepo.findById(fileId);
        String filePath = fileData.get().getFileUrl();
        byte[] data = Files.readAllBytes(new File(filePath).toPath());
        return data;
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
}
