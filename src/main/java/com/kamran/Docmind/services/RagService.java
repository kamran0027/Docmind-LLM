package com.kamran.Docmind.services;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.kamran.Docmind.DTO.Request;


@Service
public class RagService {


    private final ChatClient questionAnswerChatClient;

    private final ChatClient advanceChatClient;

    private final ChatClient simpleChatClient;

    RagService(@Qualifier("simpleChatClient") ChatClient simpleChatClient,
                        @Qualifier("questionAnswerChatClient")ChatClient questionAnswerChatClient,
                        @Qualifier("advanceRagChatClient") ChatClient advanceChatClient){
        this.questionAnswerChatClient=questionAnswerChatClient;
        this.advanceChatClient=advanceChatClient;
        this.simpleChatClient=simpleChatClient;

    }

    public String simpleChat(Request request){
        return simpleChatClient.prompt()
                .advisors(advisorSpec ->advisorSpec.param(
                                ChatMemory.CONVERSATION_ID
                                ,request.getConversationId())
                )
                .user(request.getUserQuery())
                .call()
                .content();
    }

    public String normalRAGChat(Request request){

        System.out.println("*************************************");
        System.out.println("normal chat Client");
        System.out.println(request.toString());
        System.out.println("**********************************");
        return questionAnswerChatClient.prompt()
                .advisors(advisorSpec ->advisorSpec.param(
                                ChatMemory.CONVERSATION_ID
                                ,request.getConversationId())
                )
                .advisors(advisorSpec -> advisorSpec.param(
                            QuestionAnswerAdvisor.FILTER_EXPRESSION
                            ,"conversationId=='" +request.getConversationId() +"'")
                )
                .user(request.getUserQuery())
                .call()
                .content();
    }

    public String advanceRAGChat(Request request){
        return advanceChatClient.prompt()
                .advisors(advisorSpec-> advisorSpec.param(
                                        ChatMemory.CONVERSATION_ID,
                                        request.getConversationId())
                )
                .advisors(advisorSpec-> advisorSpec.param(
                                        VectorStoreDocumentRetriever.FILTER_EXPRESSION,
                                        "conversationId == '" +request.getConversationId()+ "'")
                )
                .user(request.getUserQuery())
                .call()
                .content();
    }


}
