package com.kamran.Docmind.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kamran.Docmind.Logger.LoggingAdvisor;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository){
        return MessageWindowChatMemory.builder()
                                        .chatMemoryRepository(jdbcChatMemoryRepository)
                                        .maxMessages(20)
                                        .build();
    }

    @Bean(name = "simpleChatClient")
    public ChatClient simpleChatClient(ChatClient.Builder chatBuilder, ChatMemory chatMemory,LoggingAdvisor loggingAdvisor){

        return chatBuilder.defaultAdvisors(
                                        MessageChatMemoryAdvisor
                                            .builder(chatMemory)
                                            .build()
                                        ,loggingAdvisor
                                    ).build();
    }


    @Bean(name = "questionAnswerChatClient")
    public ChatClient questionAnswerChatClient(ChatClient.Builder chatBuilder,ChatMemory chatMemory
                        ,VectorStore vectorStore,LoggingAdvisor loggingAdvisor){


        return chatBuilder.defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                        ,QuestionAnswerAdvisor.builder(vectorStore)
                                    .searchRequest(SearchRequest.builder()
                                                    .topK(5)
                                                    .similarityThreshold(0.5)
                                                    .build()
                                                    )
                                                    .build()
                        ,loggingAdvisor
                        )
                        .build();
    }


    @Bean(name = "advanceRagChatClient")
    public ChatClient advanceRagChatClient(ChatClient.Builder chatBuilder, ChatMemory chatMemory, 
        VectorStore vectorStore,LoggingAdvisor loggingAdvisor){

        // Create seprate chat client for query writer

        ChatClient.Builder queryTransformerChatClientBuilder= chatBuilder.build().mutate();


        RetrievalAugmentationAdvisor ragAdvisor=RetrievalAugmentationAdvisor.builder()

                            // step 1 : rewrite user query

                            .queryTransformers(RewriteQueryTransformer.builder()
                                                .chatClientBuilder(queryTransformerChatClientBuilder)
                                                .build()
                            )
                            //step 2 : Retrive Documnets
                            .documentRetriever(
                                VectorStoreDocumentRetriever.builder()
                                .vectorStore(vectorStore)
                                .similarityThreshold(0.7)
                                .topK(5)
                                .build()   
                            )
                            .build();

        return chatBuilder
                        .defaultAdvisors(
                                MessageChatMemoryAdvisor.builder(chatMemory)
                                .build()
                            ,ragAdvisor
                            ,loggingAdvisor
                        ).build();
                            
                            


    }



}