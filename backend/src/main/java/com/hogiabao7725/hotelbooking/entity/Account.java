package com.hogiabao7725.hotelbooking.entity;

import com.hogiabao7725.hotelbooking.converter.AccountStatusConverter;
import com.hogiabao7725.hotelbooking.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Account extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Convert(converter = AccountStatusConverter.class)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile;

    // Helper for Two-way synchronization
    public void setProfile(Profile profile) {
        if (profile != null) profile.setAccount(this);
        this.profile = profile;
    }

}
