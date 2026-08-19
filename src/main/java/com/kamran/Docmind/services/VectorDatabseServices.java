package com.kamran.Docmind.services;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class VectorDatabseServices {

    private final VectorStore vectorStore;

    VectorDatabseServices(VectorStore vectorStore){
        this.vectorStore=vectorStore;
    }

    public void addDocuments(List<Document> documents){
        vectorStore.add(documents);
    }

    public List<Document> simaliritySearch(String query){

        return vectorStore.similaritySearch(SearchRequest.builder()
                                    .query(query)
                                    .topK(3)
                                    .build());
    
    }

    public List<Document> simaliritySearch(String query,String documentId){

        return vectorStore.similaritySearch(SearchRequest.builder()
                                    .query(query)
                                    .topK(3)
                                    .filterExpression("documentId == '" + documentId +"'")
                                    .build());
    
    }
}
