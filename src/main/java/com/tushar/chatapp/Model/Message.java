package com.tushar.chatapp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    private String messageid;
    private String Text;
    private Date date;
    private String chatid;
    private String fromUserid;
    private String toUserid;
}
