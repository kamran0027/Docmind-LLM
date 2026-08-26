package com.kamran.Docmind.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kamran.Docmind.services.ETLService;
import com.kamran.Docmind.services.FileStorageServices;
import com.kamran.Docmind.services.VectorDatabaseServices;


@RestController
@RequestMapping("/api/vector/test")
public class VectorTestController {

    public final VectorStore vectorStore;

    public final VectorDatabaseServices vectorDatabaseServices;

    public final FileStorageServices fileStorageServices;
    public final ETLService etlService;

    VectorTestController(VectorStore vectorStore,VectorDatabaseServices vectorDatabaseServices,FileStorageServices fileStorageServices,ETLService etlService){
        this.vectorStore=vectorStore;
        this.vectorDatabaseServices=vectorDatabaseServices;
        this.fileStorageServices=fileStorageServices;
        this.etlService=etlService;
    }

    @PostMapping("/add")
    public String addDocuments() {

        List<Document> documents = List.of(
                new Document("Spring Boot is a Java framework for building web applications."),
                new Document("Spring AI provides abstractions for working with AI models."),
                new Document("PGVector is a PostgreSQL extension for storing and searching vectors."),
                new Document("Gemini provides embedding models that convert text into vectors.")
        );

        vectorStore.add(documents);

        return "Documents added successfully";
    }

    @GetMapping("/search")
    public List<Document> search(@RequestParam String id,@RequestParam String query) {
        
        return vectorDatabaseServices.similaritySearch(query, id);
    }

    @PostMapping("/c")
    public Map<String,Object> processRequest(@PathVariable (required = false)String conversationId,
        @RequestParam(required = false,value = "attachment") List<MultipartFile> attachments,@RequestParam (required = false)String userQuery){

            List<String> documnetIds=new ArrayList<>();
            List<List<Document>> listDocumnets=new ArrayList<>();
            if(!attachments.isEmpty() &&  attachments!=null){

                for(MultipartFile attachment:attachments){
                    String documnetId=UUID.randomUUID().toString();
                    fileStorageServices.uploadFile(attachment, documnetId);
                    documnetIds.add(documnetId);
                    List<Document> documents=etlService.readDocument(attachment.getResource(),documnetId,"a","a");
                    listDocumnets.add(documents);
                }
            }
            return Map.of("query",userQuery
                ,"context",listDocumnets
            );
    }

}
