package com.kamran.Docmind.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Request {

    
    private String userId;
    private String conversationId;
    private String userQuery;


    @Override
    public String toString() {
        return "Request [userId=" + userId + ", conversationId=" + conversationId + ", userQuery=" + userQuery + "]";
    }

}
