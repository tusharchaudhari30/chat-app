package com.tushar.chatapp.Repository;

import com.tushar.chatapp.Model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findUserByEmail(String email);

    Mono<UserDetails> findByEmail(String email);
}
