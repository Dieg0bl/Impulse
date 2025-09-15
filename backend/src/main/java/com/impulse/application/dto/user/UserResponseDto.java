package com.impulse.application.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for User Response
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String uuid;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String bio;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private Boolean isActive;
    private Boolean isVerified;
    private Integer challengesCreated;
    private Integer challengesCompleted;
    private Integer totalPoints;

}
