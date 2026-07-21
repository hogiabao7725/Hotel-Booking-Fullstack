package com.hogiabao7725.hotelbooking.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "features")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Feature extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 55)
    private String name;

    @Column(name = "icon_url", nullable = false, length = 255)
    private String iconUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
