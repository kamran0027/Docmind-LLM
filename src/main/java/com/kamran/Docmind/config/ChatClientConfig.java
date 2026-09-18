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
        .renderer(StTemplateRenderer.builder()
                                    .startDelimiterToken('<')
                                    .endDelimiterToken('>')
                                    .build())
        .template("""
                    You are a helpful, accurate, and conversational AI assistant.

                    User Query:
                    <query>

                    Retrieved Context:
                    ---------------------
                    <question_answer_context>
                    ---------------------

                    Instructions:

                    1. First determine whether the retrieved context contains
                       information relevant to the user's query.

                    2. If relevant context exists, use it as the primary source.
                       Do not contradict it with general knowledge.

                    3. If the context is empty, irrelevant, or insufficient,
                       answer using your general knowledge.

                    4. If the context partially answers the question, use the
                       relevant context and complete the answer with general
                       knowledge when necessary.

                    5. Never mention the context, retrieval, RAG, documents,
                       or internal processing in your response.

                    6. Answer directly, accurately, and naturally.

                    7. Never fabricate information. If you genuinely don't know,
                       say so.

                    8. For explanations, use examples and step-by-step reasoning
                       when useful.
                    """)
                .build();
        return chatBuilder.defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                        ,QuestionAnswerAdvisor.builder(vectorStore)
                                    // this is custome promt templet if i remove it then it use defualt 
                                    .promptTemplate(customPromptTemplate)
                                    .searchRequest(SearchRequest.builder()
                                                    .topK(5)
                                                    .similarityThreshold(0.7)
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