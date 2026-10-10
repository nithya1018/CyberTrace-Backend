package com.cybertrace.model;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
 
import java.time.LocalDateTime;
 
@Entity
@Table(name = "chain_of_custody")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ChainOfCustody {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // Back-reference to the parent evidence. Ignored in JSON to break the
    // evidence -> custodyRecords -> evidence -> ... infinite loop.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evidence_id", nullable = false)
    @JsonIgnore
    private Evidence evidence;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handled_by", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User handledBy;
 
    @Column(nullable = false, length = 50)
    private String action;
 
    @Column(length = 200)
    private String location;
 
    @Column(length = 1000)
    private String notes;
 
    @Column(updatable = false)
    private LocalDateTime timestamp;
 
    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
 