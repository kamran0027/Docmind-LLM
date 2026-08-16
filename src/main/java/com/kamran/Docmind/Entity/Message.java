// package com.kamran.Docmind.Entity;

// import org.springframework.ai.chat.messages.MessageType;

// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.EnumeratedValue;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.OneToOne;
// import lombok.AllArgsConstructor;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;


// @Entity
// @Setter
// @Getter
// @AllArgsConstructor
// @NoArgsConstructor
// public class Message {

//     @Id
//     @GeneratedValue(strategy =GenerationType.UUID)
//     private long id;

//     @OneToOne
//     @JoinColumn(name = "conversation_id")
//     private String conversation_id;

//     @Enumerated(EnumType.STRING)
//     private MessageType role;

//     private String content;




// }
