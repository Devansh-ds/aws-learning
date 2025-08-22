package com.devansh.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "files")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String originalFilename;
    private String contentType;
    private Long size;

    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    private User uploadedBy;

    private LocalDateTime uploadedAt;

}
