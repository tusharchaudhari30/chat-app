package com.tushar.chatapp.Controller;


import com.tushar.chatapp.Model.Chat;
import com.tushar.chatapp.Model.Message;
import com.tushar.chatapp.Model.User;
import com.tushar.chatapp.Service.MessagingService;
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


@Controller
@SessionAttributes({"chat", "user"})
public class UserController {
    @Autowired
    MessagingService messagingService;


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
    public String logoutuser() {
        return "logout";
    }

    @GetMapping(value = "/")
    String home(final Principal principal, final Model model) {
        final Mono<User> userMono = this.messagingService.getUserByEmail(principal.getName());
        final Flux<Chat> chatFlux = this.messagingService.getChatListByDateAndUserid(userMono);
        final Mono<Chat> chatMono = chatFlux.next();
        final Flux<Message> messageFlux = this.messagingService.getMessagesByChat(chatMono);
        final IReactiveDataDriverContextVariable contextVariable = new ReactiveDataDriverContextVariable(chatFlux);
        model.addAttribute("chat", chatMono);
        model.addAttribute("recents", contextVariable);
        model.addAttribute("userid", userMono.map(user -> user.getId()));
        model.addAttribute("messages", messageFlux);
        return "index";
    }

    @GetMapping(value = "/api/sse/recents")
    public String recentssse(Principal principal, final Model model) {
        if (principal.getName() == null) {
            return "redirect:/";
        }
        final Mono<User> userMono = this.messagingService.getUserByEmail(principal.getName());
        final Flux<Chat> chatFlux = this.messagingService.getChatListByDateAndUserid(userMono);
        final IReactiveDataDriverContextVariable contextVariable = new ReactiveDataDriverContextVariable(chatFlux);
        model.addAttribute("recents", contextVariable);
        model.addAttribute("userid", userMono.map(user -> user.getId()));
        if (model.getAttribute("recents") == null) {
            return "redirect:/";
        }
        return "index :: #chatlistsse";
    }

    @GetMapping(value = "/chat/browse/{chatid}")
    public String browsechat(final Principal principal, final Model model, @PathVariable String chatid) {
        if (principal.getName() == null) {
            return "redirect:/";
        }
        final Mono<User> userMono = this.messagingService.getUserByEmail(principal.getName());
        final Mono<Chat> chatMono = this.messagingService.getChatbyChatID(chatid);
        final Flux<Message> messageFlux = this.messagingService.getMessagesByChat(chatMono);
        final Flux<Chat> chatFlux = this.messagingService.getChatListByDateAndUserid(userMono);
        final IReactiveDataDriverContextVariable contextVariable = new ReactiveDataDriverContextVariable(chatFlux);
        model.addAttribute("recents", contextVariable);
        model.addAttribute("userid", userMono.map(user -> user.getId()));
        model.addAttribute("chat", chatMono);
        model.addAttribute("messages", messageFlux);
        return "index";
    }


    @ResponseBody
    @GetMapping(value = "/api/sse/msghistory/{time}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Message> messageStream(final Principal principal, @PathVariable String time, final Model model) {
        if (principal.getName() == null) {
            return null;
        }
        final Mono<User> userMono = this.messagingService.getUserByEmail(principal.getName());
        final Mono<Chat> chatMono = (Mono<Chat>) model.getAttribute("chat");
        return this.messagingService.getMessageByTime(userMono, chatMono, time);
    }

    @ResponseBody
    @GetMapping(value = "/api/addchat/{email}")
    Mono<Chat> addchat(final Model model, Principal principal, @PathVariable String email) {
        if (principal.getName() == null) {
            return null;
        }
        Mono<User> userMono = messagingService.getUserByEmail(principal.getName());
        if (email == null) {
            return null;
        }
        return this.messagingService.addChatByEmail(email, userMono);
    }

    @ResponseBody
    @PostMapping(value = "/api/msg")
    Message getmessage(Principal principal, Message message, Model model) {
        if (principal.getName() == null) {
            return null;
        }
        final Mono<User> userMono = this.messagingService.getUserByEmail(principal.getName());
        final Mono<Chat> chatMono = (Mono<Chat>) model.getAttribute("chat");
        return this.messagingService.sendMessage(userMono, message, chatMono);
    }
}
