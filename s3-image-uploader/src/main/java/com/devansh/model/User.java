package com.devansh.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    private String fullname;

    private String profilePicture;

    @Column(name = "email_verified")
    private boolean isEmailVerified;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    // file storage for s3

    // Max quota allowed (50 MB = 50 * 1024 * 1024 bytes)
    private long storageQuota = 50 * 1024 * 1024;

    // Used storage in bytes
    private long usedStorage = 0;

    private int dailyUploadCount = 0;

    private LocalDate lastUploadDate;


}

























