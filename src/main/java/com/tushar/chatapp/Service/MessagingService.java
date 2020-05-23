package com.tushar.chatapp.Service;

import com.tushar.chatapp.Model.Chat;
import com.tushar.chatapp.Model.Message;
import com.tushar.chatapp.Model.User;
import com.tushar.chatapp.Repository.ChatRepository;
import com.tushar.chatapp.Repository.MessageRepository;
import com.tushar.chatapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessagingService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    MessageRepository messageRepository;

    public Mono<User> getUserByEmail(String email) {
        return this.userRepository.findUserByEmail(email);
    }

    public void saveuser(User user) {
        user.setId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        this.userRepository.save(user).subscribe();
    }

    public Flux<Chat> getChatListByDateAndUserid(Mono<User> userMono) {
        return this.chatRepository.findChatsByUsersOrderByDateDesc(userMono.map(User::getId));
    }

    public Mono<Chat> getChatbyChatID(String chatID) {
        return this.chatRepository.findById(chatID);
    }

    public Flux<Message> getMessagesByChat(Mono<Chat> chatMono) {
        return chatMono.flatMapMany(chat -> this.messageRepository.findAllByChatidOrderByDateAsc(chat.getId()));
    }

    public Flux<Message> getMessageByTime(Mono<User> userMono, Mono<Chat> chatMono, String time) {
        Mono<String> chatid = chatMono.map(chat -> chat.getId());
        var useridlist = chatMono.map(chat -> chat.getUsersidlist());
        Mono<String> fromid = useridlist.zipWith(userMono.map(User::getId)).map(fluxes -> {
            Map<String, String> s1 = fluxes.getT1();
            String s2 = fluxes.getT2();
            String id = "";
            for (String s : s1.keySet()) {
                if (!s.equals(s2)) {
                    id = s;
                }
            }
            return id;
        });
        return this.messageRepository.findAllByChatidAndFromUseridAndDateGreaterThan(chatid, fromid, new Date(time));
    }

    public Mono<Chat> addChatByEmail(String email, Mono<User> userMono) {
        Mono<User> touser = this.userRepository.findUserByEmail(email);
        Chat chat = new Chat();
        var chatmono = touser.zipWith(userMono).map(objects -> {
            var u1 = objects.getT1();
            var u2 = objects.getT2();
            Map<String, String> stringMap = new HashMap<String, String>();
            stringMap.put(u1.getId(), u1.getName());
            stringMap.put(u2.getId(), u2.getName());
            chat.setUsers(Arrays.asList(u1.getId(), u2.getId()));
            chat.setUsersidlist(stringMap);
            chat.setDate(new Date());
            return chat;
        });
        chatmono.subscribe(chat1 -> this.chatRepository.save(chat1).subscribe());
        return chatmono;
    }

    public Message sendMessage(Mono<User> userMono, Message message, Mono<Chat> chatMono) {
        message.setMessageid(null);
        chatMono.zipWith(userMono).map(flux -> {
            Chat chat = flux.getT1();
            User user = flux.getT2();
            message.setFromUserid(user.getId());
            message.setChatid(chat.getId());
            var uslist = chat.getUsersidlist();
            for (var s : uslist.keySet()) {
                if (!s.equals(message.getFromUserid())) {
                    message.setToUserid(s);
                }
            }
            chat.setRecent(message.getText());
            chat.setDate(message.getDate());
            if (message.getFromUserid() != null) {
                this.messageRepository.save(message).subscribe(System.out::println);
                this.chatRepository.save(chat).subscribe(System.out::println);
            }
            return chat;
        }).subscribe();
        return message;
    }
}
