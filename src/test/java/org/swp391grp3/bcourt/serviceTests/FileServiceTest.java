package org.swp391grp3.bcourt.serviceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.swp391grp3.bcourt.entities.Court;
import org.swp391grp3.bcourt.entities.FileData;
import org.swp391grp3.bcourt.entities.User;
import org.swp391grp3.bcourt.repo.CourtRepo;
import org.swp391grp3.bcourt.repo.FileRepo;
import org.swp391grp3.bcourt.services.FileService;
import org.swp391grp3.bcourt.services.UserService;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileRepo fileRepo;

    @Mock
    private UserService userService;

    @Mock
    private CourtRepo courtRepo;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private FileService fileService;

    @Value("${file.upload.path}")
    private String uploadPath = "uploads";

    private User user;
    private Court court;
    private FileData fileData;

    @BeforeEach
    public void setUp() {
        // Initialize user and court
        user = new User();
        user.setUserId("1");

        court = new Court();
        court.setCourtId("1");

        // Initialize FileData
        fileData = FileData.builder()
                .fileId(UUID.randomUUID().toString())
                .fileName("testfile.jpg")
                .fileUrl(uploadPath + File.separator + "testfile.jpg")
                .fileType(file.getContentType())
                .build();
    }

    @Test
    public void testUploadFileToFileSystem() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("testfile.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(userService.getUserById(anyString())).thenReturn(user);
        when(fileRepo.save(any(FileData.class))).thenReturn(fileData);

        String fileId = fileService.uploadFileToFileSystem(file, "1");

        assertNotNull(fileId);
        verify(file, times(1)).transferTo(any(File.class));
        verify(fileRepo, times(1)).save(any(FileData.class));
        verify(userService, times(1)).updateUserImg(anyString(), any(FileData.class));
    }

    @Test
    public void testUploadFileCourtToFileSystem() throws IOException {
        when(file.isEmpty()).thenReturn(false);
        when(file.getOriginalFilename()).thenReturn("testfile.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(courtRepo.findByCourtId(anyString())).thenReturn(Optional.of(court));
        when(fileRepo.save(any(FileData.class))).thenReturn(fileData);

        String fileId = fileService.uploadFileCourtToFileSystem(file, "1");

        assertNotNull(fileId);
        verify(file, times(1)).transferTo(any(File.class));
        verify(fileRepo, times(1)).save(any(FileData.class));
        verify(courtRepo, times(1)).save(any(Court.class));
    }

    @Test
    public void testGetFileFromFileSystem() throws IOException {
        when(fileRepo.findById(anyString())).thenReturn(Optional.of(fileData));

        byte[] fileBytes = fileService.getFileFromFileSystem(fileData.getFileId());

        assertNotNull(fileBytes);
        verify(fileRepo, times(1)).findById(anyString());
    }

    @Test
    public void testUploadFileToFileSystem_FileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.uploadFileToFileSystem(file, "1");
        });

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    public void testUploadFileCourtToFileSystem_FileIsEmpty() {
        when(file.isEmpty()).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.uploadFileCourtToFileSystem(file, "1");
        });

        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    public void testGetFileFromFileSystem_FileNotFound() {
        when(fileRepo.findById(anyString())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fileService.getFileFromFileSystem("nonexistentFileId");
        });

        assertEquals("File not found", exception.getMessage());
    }
}

