package com.kamran.Docmind.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kamran.Docmind.Entity.FileDetails;

public interface FileDetailsRepository extends JpaRepository<FileDetails, Long> {
    Optional<FileDetails> findByUrl(String url);    

}
