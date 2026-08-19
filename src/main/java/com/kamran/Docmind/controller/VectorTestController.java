package com.kamran.Docmind.controller;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vector")
public class VectorTestController {

    public final VectorStore vectorStore;

    VectorTestController(VectorStore vectorStore){
        this.vectorStore=vectorStore;
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
    public List<Document> search(@RequestParam String query) {

        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3)
                        .build()
        );
    }

}
