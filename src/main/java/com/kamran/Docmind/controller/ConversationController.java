package com.kamran.Docmind.controller;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kamran.Docmind.DTO.MessageDto;
import com.kamran.Docmind.DTO.Request;
import com.kamran.Docmind.Entity.Conversation;
import com.kamran.Docmind.Entity.MessageType;
import com.kamran.Docmind.services.ChatHistoryServices;
import com.kamran.Docmind.services.ChatService;
import com.kamran.Docmind.services.ETLService;
import com.kamran.Docmind.services.FileStorageServices;
import com.kamran.Docmind.services.RagService;
import com.kamran.Docmind.services.VectorDatabaseServices;

@RestController
@RequestMapping("/c")
public class ConversationController {

    private final VectorDatabaseServices vectorDatabaseServices;

    private final ChatService chatService;

    private final ETLService etlService;

    private final FileStorageServices fileStorageServices;

    private final ChatHistoryServices chatHistoryServices;

    private final RagService ragService;


    ConversationController(ChatService chatService,ETLService etlService,FileStorageServices fileStorageServices,
        VectorDatabaseServices vectorDatabaseServices,ChatHistoryServices chatHistoryServices,RagService ragService) {
        this.chatService = chatService;
        this.etlService=etlService;
        this.fileStorageServices=fileStorageServices;
        this.vectorDatabaseServices = vectorDatabaseServices;
        this.chatHistoryServices=chatHistoryServices;
        this.ragService=ragService;
                            
    }

    @GetMapping("/{conversationId}")
    public List<MessageDto> getConversationMessages(@PathVariable String conversationId) {
        return chatService.getConversationMessages(conversationId);
    }

    @PostMapping("/")
    public  MessageDto processRequest(
        @RequestParam(required = false,value = "attachment") List<MultipartFile> attachments,
        @RequestParam(required = false) String userId, @RequestParam (required = false) String conversationId,
        @RequestParam String userQuery){

            Request request=new Request(userId,conversationId,userQuery);

            if(request.getConversationId()==null  
                || request.getConversationId().isBlank()){

                Conversation conversation=chatHistoryServices.createConversation(false);
                request.setConversationId(conversation.getId());
            }

            System.out.println("**********************************");
            System.out.println(request.toString());
            System.out.println("**********************************");
            // save user query for history
            chatHistoryServices.saveUserMessage(request.getConversationId(),request.getUserQuery());

            List<String> documnetIds=new ArrayList<>();
            if(attachments!=null && !attachments.isEmpty()){

                for(MultipartFile attachment:attachments){
                    String documnetId=UUID.randomUUID().toString();
                    fileStorageServices.uploadFile(attachment, documnetId);
                    documnetIds.add(documnetId);
                    List<Document> documents=etlService.readDocument(attachment.getResource(),documnetId,request.getConversationId(),request.getUserId());
                }
            }

            String response=ragService.normalRAGChat(request);
            chatHistoryServices.saveAssistantMessage(request.getConversationId(),response);
            return new MessageDto(request.getConversationId(),MessageType.ASSISTANT,response,LocalDateTime.now());
            
    }



}
