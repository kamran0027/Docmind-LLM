package com.kamran.Docmind.controller;

import java.util.List;

import org.apache.http.protocol.HTTP;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kamran.Docmind.services.VectorDatabseServices;

@RestController
@RequestMapping("/api/vector")
public class VectorTestController {

    public final VectorStore vectorStore;

    public final VectorDatabseServices vectorDatabseServices;

    VectorTestController(VectorStore vectorStore,VectorDatabseServices vectorDatabseServices){
        this.vectorStore=vectorStore;
        this.vectorDatabseServices=vectorDatabseServices;
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
        
        return vectorDatabseServices.simaliritySearch(query, id);
    }

}
