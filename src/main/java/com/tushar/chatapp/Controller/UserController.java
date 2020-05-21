package com.tushar.chatapp.Controller;


import com.tushar.chatapp.Model.Chat;
import com.tushar.chatapp.Model.Message;
import com.tushar.chatapp.Model.User;
import com.tushar.chatapp.Repository.ChatRepository;
import com.tushar.chatapp.Repository.MessageRepository;
import com.tushar.chatapp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Arrays;
import java.util.Date;


@Controller
@SessionAttributes({"chat", "user"})
public class UserController {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    UserRepository userRepository;


    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    // Login form with error
    @RequestMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }
    @RequestMapping("/logoutuser")
    public String logoutuser(){
        return "logout";
    }

    @GetMapping(value = "/")
    String home(Principal principal, final Model model) {
        Mono<User> userMono = this.userRepository.findUserByEmail(principal.getName());
        Flux<Chat> chatFlux = this.chatRepository.findChatsByUsersidListOrderByDateDesc(userMono.map(user -> user.getId()));
        Mono<Chat> firstMono = chatFlux.next();
        Flux<Message> messageFlux = firstMono.flatMapMany(chat -> this.messageRepository.findAllByChatidOrderByDateAsc(chat.getId()));
        final IReactiveDataDriverContextVariable contextVariable = new ReactiveDataDriverContextVariable(chatFlux);
        model.addAttribute("user", userMono);
        model.addAttribute("chat", firstMono);
        model.addAttribute("recents", contextVariable);
        model.addAttribute("userid", userMono.map(user -> user.getId()));
        model.addAttribute("messages", messageFlux);
        return "index";
    }

    @GetMapping(value = "/api/sse/recents")
    public String recentssse(Principal principal, final Model model) {
        Mono<User> userMono = (Mono<User>) model.getAttribute("user");
        if (userMono == null) {
            return null;
        }
        Flux<Chat> chatFlux = this.chatRepository.findChatsByUsersidListOrderByDateDesc(userMono.map(User::getId));
        chatFlux.subscribe();
        final IReactiveDataDriverContextVariable contextVariable = new ReactiveDataDriverContextVariable(chatFlux);
        model.addAttribute("recents", contextVariable);
        return "index :: #chatlistsse";
    }

    @GetMapping(value = "/chat/browse/{chatid}")
    public String browsechat(Principal principal, final Model model, @PathVariable String chatid) {
        Mono<User> userMono = (Mono<User>) model.getAttribute("user");
        Flux<Message> messageFlux = this.messageRepository.findAllByChatidOrderByDateAsc(chatid);
        messageFlux.subscribe();
        Mono<Chat> chatMono = this.chatRepository.findChatById(chatid);
        chatMono.subscribe();
        Flux<Chat> chatFlux = this.chatRepository.findChatsByUsersidListOrderByDateDesc(userMono.map(User::getId));
        final IReactiveDataDriverContextVariable contextVariable = new ReactiveDataDriverContextVariable(chatFlux);
        model.addAttribute("recents", contextVariable);
        model.addAttribute("userid", userMono.map(user -> user.getId()));
        model.addAttribute("chat", chatMono);
        model.addAttribute("messages", messageFlux);
        return "index";
    }


    @ResponseBody
    @GetMapping(value = "/api/sse/msghistory/{time}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> messageStream(Principal principal, @PathVariable String time, final Model model) {
        Mono<User> userMono = (Mono<User>) model.getAttribute("user");
        if (userMono == null) {
            return null;
        }
        Mono<String> userid = userMono.map(user -> user.getId());
        Mono<Chat> chatMono = (Mono<Chat>) model.getAttribute("chat");
        Mono<String> chatid = chatMono.map(chat -> chat.getId());
        var useridlist = chatMono.map(chat -> chat.getUsersidList());
        Mono<String> fromid = useridlist.zipWith(userid).map(fluxes -> {
            var s1 = fluxes.getT1();
            var s2 = fluxes.getT2();
            String id = "";
            for (var s : s1) {
                if (!s.equals(s2)) {
                    id = s;
                }
            }
            return id;
        });
        final Flux<Message> messageFlux = this.messageRepository.findAllByChatidAndFromUseridAndDateGreaterThan(chatid, fromid, new Date(time));
        return messageFlux;
    }

    @ResponseBody
    @GetMapping(value = "/api/addchat/{email}")
    Mono<Chat> addchat(final Model model, Principal principal, @PathVariable String email) {
        Mono<User> userMono = (Mono<User>) model.getAttribute("user");
        if (email == null) {
            return null;
        }

        Mono<User> touser = this.userRepository.findUserByEmail(email);
        Chat chat = new Chat();
        var chatmono = touser.zipWith(userMono).map(objects -> {
            var u1 = objects.getT1();
            var u2 = objects.getT2();
            chat.setUsersidList(Arrays.asList(u1.getId(), u2.getId()));
            chat.setDate(new Date());
            chat.setName(u1.getName());
            return chat;
        });
        chatmono.subscribe(chat1 -> this.chatRepository.save(chat1).subscribe());
        return chatmono;
    }

    @ResponseBody
    @PostMapping(value = "/api/msg")
    Message getmessage(Principal principal, Message message, Model model) {
        Mono<User> user = (Mono<User>) model.getAttribute("user");
        message.setMessageid(null);
        Mono<Chat> chatMono = (Mono<Chat>) model.getAttribute("chat");
        chatMono.subscribe();
        chatMono.zipWith(user).map(flux -> {
            Chat chat=flux.getT1();
            User user1=flux.getT2();
            message.setFromUserid(user1.getId());
            message.setChatid(chat.getId());
            var uslist = chat.getUsersidList();
            for (var s : uslist) {
                if (!s.equals(message.getFromUserid())) {
                    message.setToUserid(s);
                }
            }
            chat.setRecent(message.getText());
            chat.setDate(message.getDate());
            if(message.getFromUserid()!=null) {
                this.messageRepository.save(message).subscribe(System.out::println);
                this.chatRepository.save(chat).subscribe(System.out::println);
            }
            return chat;
        }).subscribe();

        return message;
    }
}
