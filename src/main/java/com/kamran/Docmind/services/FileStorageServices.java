package com.kamran.Docmind.services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kamran.Docmind.Entity.FileDetails;
import com.kamran.Docmind.repository.FileDetailsRepository;

@Service
public class FileStorageServices {

    public static final String UPLOAD_DIR = "uploads/";


    private final FileDetailsRepository fileDetailsRepository;

    public FileStorageServices(FileDetailsRepository fileDetailsRepository) {
        this.fileDetailsRepository = fileDetailsRepository;
    }

    public Boolean uploadFile(MultipartFile file) {
        try {
            File directory=new File(UPLOAD_DIR);

            if (!directory.exists()) {

                System.out.println("Directoiry created : "+UPLOAD_DIR);
                directory.mkdir();
            }

            Path filPath=Paths.get(UPLOAD_DIR+file.getOriginalFilename());
            Files.write(filPath,file.getBytes());

            
            FileDetails fileDetails = new FileDetails();
            fileDetails.setName(file.getOriginalFilename());
            fileDetails.setUrl(filPath.toString());
            fileDetails.setType(file.getContentType());

            fileDetailsRepository.save(fileDetails);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public String getFilePathById(long id) {
        FileDetails fileDetails = fileDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
        return fileDetails.getUrl();
    }





}
