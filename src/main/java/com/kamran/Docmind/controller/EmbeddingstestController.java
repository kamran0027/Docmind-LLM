package com.kamran.Docmind.controller;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/embeddingstest")
public class EmbeddingstestController {


    private final EmbeddingModel embeddingModel;

    EmbeddingstestController(EmbeddingModel embeddingModel){
        this.embeddingModel=embeddingModel;
    }


    @GetMapping("/test")
    public Map<String,Object> testEmbeddingModel(@RequestParam String text) {
        float[] vector = embeddingModel.embed(text);

        return Map.of(
                "text",text,
                "dimension",vector.length,
                "vector",Arrays.copyOf(vector,Math.min(100,vector.length))

        );
    }

}
