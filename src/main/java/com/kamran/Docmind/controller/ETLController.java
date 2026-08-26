package com.kamran.Docmind.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.kamran.Docmind.services.ETLService;
import com.kamran.Docmind.services.FileStorageServices;


@RestController
@RequestMapping("api")
public class ETLController {

    private final FileStorageServices fileStorageServices;
    private final ETLService etlService;

    public ETLController(FileStorageServices fileStorageServices, ETLService etlService) {
        this.fileStorageServices = fileStorageServices;
        this.etlService = etlService;
    }

    @PostMapping("/uploads")
    public Map<String,Object> uploadDocument(@RequestParam ("file") MultipartFile file){
        String uuid=UUID.randomUUID().toString();
        if(fileStorageServices.uploadFile(file,uuid)){
            return Map.of("document_Id",uuid,
                            "file_name",file.getOriginalFilename()

            );
        }else{
            return Map.of("error","error in uploading file :"+file.getOriginalFilename());
        }       
    }
    @GetMapping("/documents/{id}")
    public List<Document> getAllDocuments(@PathVariable("id") String id){
        return etlService.readDocument(id,"0","0");
    }

    @PostMapping("/documents")
    public List<Document> getAllDocuments(@RequestParam("file") MultipartFile file){
        try{
            Resource resource = file.getResource();
            String uuid=UUID.randomUUID().toString();
            System.out.println("*************************************************************");
            System.out.println("Resource : "+resource.getFilename());
            System.out.println("document id : " + uuid);
            System.out.println("*************************************************************");
            
            List<Document> documents= etlService.readDocument(resource,uuid,"a","a");
            return documents;

        } catch (Exception e) {
            throw new RuntimeException("Failed to read document", e);
        }
    }

}
