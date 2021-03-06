package com.tushar.chatapp.Repository;

import com.tushar.chatapp.Model.Chat;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    Flux<Chat> findChatsByUsersOrderByDateDesc(Mono<String> id);

    Mono<Chat> findChatById(String chatid);
}
