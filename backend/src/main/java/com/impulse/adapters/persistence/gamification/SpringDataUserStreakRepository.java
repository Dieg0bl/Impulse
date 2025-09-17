package com.impulse.adapters.persistence.gamification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataUserStreakRepository extends JpaRepository<UserStreakJpaEntity, String> {

    List<UserStreakJpaEntity> findByUserId(String userId);

    Optional<UserStreakJpaEntity> findByUserIdAndStreakType(String userId, String streakType);

    List<UserStreakJpaEntity> findByIsActiveTrue();

    List<UserStreakJpaEntity> findByStreakType(String streakType);

    @Query("SELECT u FROM UserStreakJpaEntity u WHERE u.currentStreak >= :minStreak")
    List<UserStreakJpaEntity> findByCurrentStreakGreaterThanEqual(@Param("minStreak") int minStreak);

    @Query("SELECT u FROM UserStreakJpaEntity u WHERE u.longestStreak >= :minStreak")
    List<UserStreakJpaEntity> findByLongestStreakGreaterThanEqual(@Param("minStreak") int minStreak);

    @Query("SELECT u FROM UserStreakJpaEntity u WHERE u.userId = :userId AND u.isActive = true")
    List<UserStreakJpaEntity> findActiveStreaksByUserId(@Param("userId") String userId);
}
