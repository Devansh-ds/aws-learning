package com.devansh.repo;

import com.devansh.model.FileMetadata;
import com.devansh.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Integer> {

    List<FileMetadata> findByKeyInAndUploadedBy(List<String> keys, User uploadedBy);

}
