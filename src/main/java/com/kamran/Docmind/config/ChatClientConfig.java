package com.kamran.Docmind.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.template.st.StTemplateRenderer;
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

        PromptTemplate customPromptTemplate = PromptTemplate.builder()
        .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
        .template("""
            <query>

            Context information is below.

			---------------------
			<question_answer_context>
			---------------------

			Given the context information and no prior knowledge, answer the query.

			Follow these rules:

			1. If the answer is in the context then only give answer based on provided context and if context is not present then give answer based on your knowledge base.
            
			2. Avoid statements like "Based on the context..." or "The provided information..." or "since context is missing ...".
        """)
        .build();
        return chatBuilder.defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                        ,QuestionAnswerAdvisor.builder(vectorStore)
                                    // this is custome promt templet if i remove it then it use defualt 
                                    .promptTemplate(customPromptTemplate)
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
                                .similarityThreshold(0.70)
                                .topK(5)
                                .build()   
                            )
                            .queryAugmenter(ContextualQueryAugmenter.builder() 
                                                .allowEmptyContext(true)
                                                .build())
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