package com.kamran.Docmind.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ConversationDto {

    private String id;
    private boolean temporary;
    private String title;
    private String createdAt;

}
