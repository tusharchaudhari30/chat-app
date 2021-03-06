package com.tushar.chatapp.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Chat {
    @Id
    private String id;
    private Map<String, String> usersidlist;
    private Date date;
    private String recent;
    private List<String> users;


}
