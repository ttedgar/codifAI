package com.edi.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "challenges")
@Getter
@Setter
@ToString(exclude = {"description", "starterCode", "hiddenTests", "sampleTests"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String starterCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String hiddenTests;

    @Column(columnDefinition = "TEXT")
    private String sampleTests;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "challenge_tags", joinColumns = @JoinColumn(name = "challenge_id"))
    @Column(name = "tag")
    private List<String> tags;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
