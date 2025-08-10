package com.impulse.domain.tutor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="validations")
public class Validation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long retoId;
    private Long validatorId;
    private String status;
    @Column(columnDefinition="TEXT")
    private String feedback;
    private LocalDateTime createdAt = LocalDateTime.now();
    public Validation(){}
    public Validation(Long retoId, Long validatorId, String status, String feedback){this.retoId=retoId;this.validatorId=validatorId;this.status=status;this.feedback=feedback;}
    public Long getId(){return id;}
    public Long getRetoId(){return retoId;}
    public Long getValidatorId(){return validatorId;}
    public String getStatus(){return status;}
    public String getFeedback(){return feedback;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
