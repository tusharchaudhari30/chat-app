package com.tushar.chatapp.Repository;

import com.tushar.chatapp.Model.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
    Flux<Message> findAllByChatidOrderByDateAsc(String chatid);

    @Tailable
    Flux<Message> findAllByChatidAndFromUseridAndDateGreaterThan(Mono<String> chatid, Mono<String> fromid, Date date);
}
