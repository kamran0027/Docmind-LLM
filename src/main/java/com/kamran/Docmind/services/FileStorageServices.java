package com.kamran.Docmind.services;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kamran.Docmind.Entity.Attachment;
import com.kamran.Docmind.repository.AttachmentRepository;



@Service
public class FileStorageServices {

    public static final String UPLOAD_DIR = "uploads/";


    private final AttachmentRepository attachmentRepository;

    public FileStorageServices(AttachmentRepository attachmentRepository) {
        this.attachmentRepository =attachmentRepository;
    }

    public boolean uploadFile(MultipartFile file,String uuid) {
        try {
            File directory=new File(UPLOAD_DIR);

            if (!directory.exists()) {

                System.out.println("Directoiry created : "+UPLOAD_DIR);
                directory.mkdir();
            }
            Path filPath=Paths.get(UPLOAD_DIR+file.getOriginalFilename()+uuid);
            Files.write(filPath,file.getBytes());

            Attachment attachment = new Attachment();
            attachment.setName(file.getOriginalFilename());
            attachment.setUrl(filPath.toString());
            attachment.setType(file.getContentType());
            attachment.setDocumnetId(uuid);

            attachmentRepository.save(attachment);

            return true;

        } catch (Exception e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    public String getFilePathById(String documentId) {
        Attachment attachment = attachmentRepository.findByDocumnetId(documentId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + documentId));
        return attachment.getUrl();
    }





}
