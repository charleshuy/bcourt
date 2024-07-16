package org.swp391grp3.bcourt.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.FileData;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.CourtRepo;
import org.swp391grp3.bcourt.repo.FileRepo;
import org.swp391grp3.bcourt.repo.UserRepo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepo fileRepo;
    private final UserRepo userRepo;
    private final CourtRepo courtRepo;

    @Value("${file.upload.path}") // Injecting property from application.properties
    private String uploadPath;

    public String uploadFileToFileSystem(MultipartFile file, String userId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Retrieve the user's current profile photo (if exists)
        User user = userRepo.findByUserId(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with userId: " + userId);
        }

        FileData oldFileData = user.getFile();
        deleteOldFile(oldFileData);

        // Generate a unique file name for the new file
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + extension;

        // Construct the full file path using the injected uploadPath
        String filePath = uploadPath + File.separator + fileName;

        try {
            // Save the file to the file system
            file.transferTo(new File(filePath));

            // Save file metadata to database
            FileData savedFileData = fileRepo.save(FileData.builder()
                    .fileName(originalFilename)
                    .fileUrl(filePath) // Store the absolute file path
                    .fileType(file.getContentType())
                    .build());

            // Update user's profile photo (assuming userId is used as fileId)
            user.setFile(savedFileData); // Update user's file reference
            userRepo.save(user);

            return savedFileData.getFileId();
        } catch (IOException e) {
            log.error("Failed to store file {}", originalFilename, e);
            throw e; // Rethrow the exception or handle accordingly
        }
    }
    public void deleteOldFile(FileData oldFileData){
        if (oldFileData != null) {
            // Delete the old file from the file system
            String oldFilePath = oldFileData.getFileUrl();
            File oldFile = new File(oldFilePath);
            if (oldFile.exists()) {
                boolean deleted = oldFile.delete();
                if (!deleted) {
                    log.warn("Failed to delete old file: {}", oldFilePath);
                }
            }
            // Delete old file metadata from database
            fileRepo.delete(oldFileData);
        }
    }
    public void deleteFile(String fileId) {
        Optional<FileData> fileDataOptional = fileRepo.findById(fileId);
        if (fileDataOptional.isPresent()) {
            FileData fileData = fileDataOptional.get();

            // Delete the file from the file system
            String filePath = fileData.getFileUrl();
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.warn("Failed to delete file: {}", filePath);
                }
            }

            // Delete the file metadata from the database
            fileRepo.delete(fileData);
        } else {
            throw new IllegalArgumentException("File not found with fileId: " + fileId);
        }
    }

    public String uploadFileCourtToFileSystem(MultipartFile file, String courtId) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Retrieve the court by courtId
        Optional<Court> optionalCourt = courtRepo.findByCourtId(courtId);
        Court court = optionalCourt.orElseThrow(() -> new IllegalArgumentException("Court not found with courtId: " + courtId));

        FileData oldFileData = court.getFile();
        deleteOldFile(oldFileData);

        // Generate a unique file name for the new file
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + extension;

        // Construct the full file path using the injected uploadPath
        String filePath = uploadPath + File.separator + fileName;

        try {
            // Save the file to the file system
            file.transferTo(new File(filePath));

            // Save file metadata to database
            FileData savedFileData = fileRepo.save(FileData.builder()
                    .fileName(originalFilename)
                    .fileUrl(filePath) // Store the absolute file path
                    .fileType(file.getContentType())
                    .build());

            // Update court's file reference
            court.setFile(savedFileData);
            courtRepo.save(court);

            return savedFileData.getFileId();
        } catch (IOException e) {
            log.error("Failed to store file {}", originalFilename, e);
            throw e; // Rethrow the exception or handle accordingly
        }
    }

    public byte[] getFileFromFileSystem(String fileId) throws IOException {
        Optional<FileData> fileData = fileRepo.findById(fileId);
        String filePath = fileData.orElseThrow(() -> new IllegalArgumentException("File not found")).getFileUrl();
        return Files.readAllBytes(new File(filePath).toPath());
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }


}
