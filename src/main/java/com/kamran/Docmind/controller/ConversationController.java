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

    @GetMapping("/{id}")
    public List<MessageDto> getConversationMessages(@PathVariable("id") String conversationId) {
        System.out.println("*******************************");
        System.out.println(conversationId);
        return chatService.getConversationMessages(conversationId);
    }

    @PostMapping("/")
    public  MessageDto processRequest(
        @RequestParam(required = false,value = "attachment") List<MultipartFile> attachments,
        @RequestParam(required = false) String userId, @RequestParam (required = false) String conversationId,
        @RequestParam String userQuery){

            System.out.println("\n=== CONVERSATION CONTROLLER - processRequest ===");
            System.out.println("Received request:");
            System.out.println("  userQuery: " + userQuery);
            System.out.println("  conversationId: " + conversationId);
            System.out.println("  userId: " + userId);
            System.out.println("  attachments count: " + (attachments != null ? attachments.size() : 0));
            if (attachments != null && !attachments.isEmpty()) {
                for (int i = 0; i < attachments.size(); i++) {
                    MultipartFile file = attachments.get(i);
                    System.out.println("    [" + i + "] " + file.getOriginalFilename() + 
                        " (" + file.getSize() + " bytes, " + file.getContentType() + ")");
                }
            }
            System.out.println("================================================\n");

            Request request=new Request(userId,conversationId,userQuery);

            if(request.getConversationId()==null  
                || request.getConversationId().isBlank()){

                Conversation conversation=chatHistoryServices.createConversation(false);
                request.setConversationId(conversation.getId());
                System.out.println("Created new conversation: " + conversation.getId());
            }

            System.out.println("***** Processing Request *****");
            System.out.println(request.toString());
            System.out.println("*****************************");
            
            // save user query for history
            try {
                chatHistoryServices.saveUserMessage(request.getConversationId(),request.getUserQuery());
                System.out.println("✓ Saved user message to conversation: " + request.getConversationId());
            } catch (Exception e) {
                System.err.println("✗ Error saving user message: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Failed to save user message: " + e.getMessage());
            }

            List<String> documnetIds=new ArrayList<>();
            if(attachments!=null && !attachments.isEmpty()){
                System.out.println("Processing " + attachments.size() + " attachments...");
                for(MultipartFile attachment:attachments){
                    try {
                        String documnetId=UUID.randomUUID().toString();
                        System.out.println("  Processing: " + attachment.getOriginalFilename());
                        fileStorageServices.uploadFile(attachment, documnetId);
                        documnetIds.add(documnetId);
                        System.out.println("    ✓ File uploaded with ID: " + documnetId);
                        
                        List<Document> documents=etlService.readDocument(attachment.getResource(),documnetId,request.getConversationId(),request.getUserId());
                        System.out.println("    ✓ Extracted " + (documents != null ? documents.size() : 0) + " documents");
                    } catch (Exception e) {
                        System.err.println("    ✗ Error processing attachment: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                String response=ragService.normalRAGChat(request);
                chatHistoryServices.saveAssistantMessage(request.getConversationId(),response);
                System.out.println("✓ Response generated and saved");
                return new MessageDto(request.getConversationId(),MessageType.ASSISTANT,response,LocalDateTime.now());
            }

            String response=ragService.normalRAGChat(request);
            chatHistoryServices.saveAssistantMessage(request.getConversationId(),response);
            System.out.println("✓ Response generated and saved (no attachments)");
            return new MessageDto(request.getConversationId(),MessageType.ASSISTANT,response,LocalDateTime.now());
            
    }



}
