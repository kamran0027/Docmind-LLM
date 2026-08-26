package com.kamran.Docmind.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kamran.Docmind.Entity.Attachment;




public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Optional<Attachment> findByUrl(String url);
    Optional<Attachment> findByDocumnetId(String documnetId);

}
