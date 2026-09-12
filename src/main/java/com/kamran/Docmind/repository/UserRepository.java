package com.kamran.Docmind.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kamran.Docmind.Entity.User;
import java.util.Optional;




public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderSubject(String provider, String providerSubject);

    Optional<User> findByEmail(String email);

}
