package com.kamran.Docmind.controller;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kamran.Docmind.services.FileStorageServices;
import com.kamran.Docmind.services.PdfReader;

@RestController
@RequestMapping("api")
public class ETLController {

    private final FileStorageServices fileStorageServices;
    private final PdfReader pdfReader;

    public ETLController(FileStorageServices fileStorageServices, PdfReader pdfReader) {
        this.fileStorageServices = fileStorageServices;
        this.pdfReader = pdfReader;
    }

    @PostMapping("/uploads")
    public String uploadDocument(@RequestParam ("file") MultipartFile file){

        if(fileStorageServices.uploadFile(file)){
            return "file uploaded successfully : "+file.getOriginalFilename();
        }else{
            return "file upload failed : "+file.getOriginalFilename();
        }       
    }
    @GetMapping("/documents/{id}")
    public List<Document> getAllDocuments(@PathVariable("id") Long id){
        return pdfReader.readDocument(id);
    }

    @PostMapping("/documents")
    public List<Document> getAllDocuments(@RequestParam("file") MultipartFile file){
        try{
            Resource resource = file.getResource();
            System.out.println("*************************************************************");
            System.out.println("Resource : "+resource.getFilename());
            System.out.println("*************************************************************");
            return pdfReader.readDocument(resource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read document", e);
        }
    }

}
