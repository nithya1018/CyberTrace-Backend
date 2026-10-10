package com.cybertrace.model;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
@Entity
@Table(name = "evidence")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Evidence {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // Back-reference to the parent case. Ignored in JSON to break the
    // case -> evidence -> case -> ... infinite loop.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    @JsonIgnore
    private ForensicCase forensicCase;
 
    @Column(nullable = false, length = 200)
    private String fileName;
 
    @Column(length = 50)
    private String fileType;
 
    @Column(length = 2000)
    private String description;
 
    @Column(length = 128)
    private String hash;
 
    // Server-side disk path: not needed by the frontend and shouldn't be exposed.
    @Column(length = 500)
    @JsonIgnore
    private String storagePath;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User uploadedBy;
 
    // "evidence" is ignored inside each custody record to stop
    // evidence -> custodyRecords -> evidence -> ... from repeating.
    @OneToMany(mappedBy = "evidence", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"evidence", "hibernateLazyInitializer", "handler"})
    @Builder.Default
    private List<ChainOfCustody> custodyRecords = new ArrayList<>();
 
    @Column(updatable = false)
    private LocalDateTime uploadedAt;
 
    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}
 