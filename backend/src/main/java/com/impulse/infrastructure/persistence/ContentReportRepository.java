package com.impulse.infrastructure.persistence;

import com.impulse.domain.model.ContentReport;
import com.impulse.domain.model.enums.ContentReportStatus;
import com.impulse.domain.model.enums.ContentReportType;
import com.impulse.domain.model.enums.ReportedContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para gestión de reportes de contenido
 */
@Repository
public interface ContentReportRepository extends JpaRepository<ContentReport, Long> {

    // ===== BÚSQUEDAS BÁSICAS =====
    
    List<ContentReport> findByStatus(ContentReportStatus status);
    
    Page<ContentReport> findByStatus(ContentReportStatus status, Pageable pageable);
    
    List<ContentReport> findByReportType(ContentReportType reportType);
    
    Page<ContentReport> findByReportType(ContentReportType reportType, Pageable pageable);
    
    List<ContentReport> findByContentType(ReportedContentType contentType);
    
    Page<ContentReport> findByContentType(ReportedContentType contentType, Pageable pageable);
    
    // ===== BÚSQUEDAS POR USUARIO =====
    
    List<ContentReport> findByReporterId(Long reporterId);
    
    Page<ContentReport> findByReporterId(Long reporterId, Pageable pageable);
    
    List<ContentReport> findByReportedUserId(Long reportedUserId);
    
    Page<ContentReport> findByReportedUserId(Long reportedUserId, Pageable pageable);
    
    List<ContentReport> findByModeratorId(Long moderatorId);
    
    Page<ContentReport> findByModeratorId(Long moderatorId, Pageable pageable);
    
    // ===== BÚSQUEDAS POR CONTENIDO =====
    
    List<ContentReport> findByContentIdAndContentType(Long contentId, ReportedContentType contentType);
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.contentId = :contentId AND cr.contentType = :contentType AND cr.status = :status")
    List<ContentReport> findByContentAndStatus(@Param("contentId") Long contentId, 
                                              @Param("contentType") ReportedContentType contentType,
                                              @Param("status") ContentReportStatus status);
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.contentId = :contentId AND cr.contentType = :contentType")
    Long countReportsByContent(@Param("contentId") Long contentId, @Param("contentType") ReportedContentType contentType);
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.contentId = :contentId AND cr.contentType = :contentType AND cr.status = 'CONFIRMED'")
    Long countConfirmedReportsByContent(@Param("contentId") Long contentId, @Param("contentType") ReportedContentType contentType);
    
    // ===== BÚSQUEDAS POR FECHAS =====
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.createdAt BETWEEN :startDate AND :endDate")
    List<ContentReport> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.createdAt BETWEEN :startDate AND :endDate")
    Page<ContentReport> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.createdAt >= :since")
    List<ContentReport> findReportsAfter(@Param("since") LocalDateTime since);
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.reviewedAt BETWEEN :startDate AND :endDate")
    List<ContentReport> findReviewedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // ===== REPORTES PENDIENTES =====
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.status = 'PENDING' ORDER BY cr.createdAt ASC")
    List<ContentReport> findPendingReports();
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.status = 'PENDING' ORDER BY cr.createdAt ASC")
    Page<ContentReport> findPendingReports(Pageable pageable);
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.status = 'PENDING' AND cr.priority = :priority ORDER BY cr.createdAt ASC")
    List<ContentReport> findPendingReportsByPriority(@Param("priority") String priority);
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.status = 'PENDING'")
    Long countPendingReports();
    
    // ===== ESTADÍSTICAS GLOBALES =====
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.status = :status")
    Long countByStatus(@Param("status") ContentReportStatus status);
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.reportType = :type")
    Long countByReportType(@Param("type") ContentReportType type);
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.contentType = :type")
    Long countByContentType(@Param("type") ReportedContentType type);
    
    @Query("SELECT cr.status, COUNT(cr) FROM ContentReport cr GROUP BY cr.status")
    List<Object[]> getReportStatusDistribution();
    
    @Query("SELECT cr.reportType, COUNT(cr) FROM ContentReport cr GROUP BY cr.reportType")
    List<Object[]> getReportTypeDistribution();
    
    @Query("SELECT cr.contentType, COUNT(cr) FROM ContentReport cr GROUP BY cr.contentType")
    List<Object[]> getContentTypeDistribution();
    
    // ===== ESTADÍSTICAS POR MODERADOR =====
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.moderatorId = :moderatorId AND cr.status != 'PENDING'")
    Long countReportsReviewedByModerator(@Param("moderatorId") Long moderatorId);
    
    @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', HOUR, cr.createdAt, cr.reviewedAt)) FROM ContentReport cr WHERE cr.moderatorId = :moderatorId AND cr.reviewedAt IS NOT NULL")
    Double getAverageReviewTimeByModerator(@Param("moderatorId") Long moderatorId);
    
    @Query("SELECT cr.moderatorId, COUNT(cr) FROM ContentReport cr WHERE cr.moderatorId IS NOT NULL GROUP BY cr.moderatorId ORDER BY COUNT(cr) DESC")
    List<Object[]> getModeratorWorkload();
    
    // ===== ESTADÍSTICAS DE USUARIOS =====
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.reporterId = :userId")
    Long countReportsByUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.reportedUserId = :userId")
    Long countReportsAgainstUser(@Param("userId") Long userId);
    
    @Query("SELECT cr.reportedUserId, COUNT(cr) FROM ContentReport cr WHERE cr.status = 'CONFIRMED' GROUP BY cr.reportedUserId HAVING COUNT(cr) >= :threshold ORDER BY COUNT(cr) DESC")
    List<Object[]> findUsersWithManyConfirmedReports(@Param("threshold") Integer threshold);
    
    @Query("SELECT cr.reporterId, COUNT(cr) FROM ContentReport cr GROUP BY cr.reporterId HAVING COUNT(cr) >= :threshold ORDER BY COUNT(cr) DESC")
    List<Object[]> findFrequentReporters(@Param("threshold") Integer threshold);
    
    // ===== TENDENCIAS TEMPORALES =====
    
    @Query("SELECT FUNCTION('DATE', cr.createdAt), COUNT(cr) FROM ContentReport cr WHERE cr.createdAt >= :since GROUP BY FUNCTION('DATE', cr.createdAt) ORDER BY FUNCTION('DATE', cr.createdAt)")
    List<Object[]> getDailyReportCounts(@Param("since") LocalDateTime since);
    
    @Query("SELECT FUNCTION('YEAR', cr.createdAt), FUNCTION('MONTH', cr.createdAt), COUNT(cr) FROM ContentReport cr GROUP BY FUNCTION('YEAR', cr.createdAt), FUNCTION('MONTH', cr.createdAt) ORDER BY FUNCTION('YEAR', cr.createdAt) DESC, FUNCTION('MONTH', cr.createdAt) DESC")
    List<Object[]> getMonthlyReportTrends();
    
    @Query("SELECT FUNCTION('HOUR', cr.createdAt), COUNT(cr) FROM ContentReport cr WHERE cr.createdAt >= :since GROUP BY FUNCTION('HOUR', cr.createdAt) ORDER BY FUNCTION('HOUR', cr.createdAt)")
    List<Object[]> getHourlyReportDistribution(@Param("since") LocalDateTime since);
    
    // ===== DUPLICADOS Y RELACIONADOS =====
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.contentId = :contentId AND cr.contentType = :contentType AND cr.reporterId != :reporterId")
    List<ContentReport> findSimilarReports(@Param("contentId") Long contentId, 
                                          @Param("contentType") ReportedContentType contentType,
                                          @Param("reporterId") Long reporterId);
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.reportedUserId = :userId AND cr.createdAt >= :since")
    List<ContentReport> findRecentReportsAgainstUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);
    
    // ===== REPORTES DE ALTA PRIORIDAD =====
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.reportType IN ('HARASSMENT', 'HATE_SPEECH', 'THREAT') AND cr.status = 'PENDING' ORDER BY cr.createdAt ASC")
    List<ContentReport> findHighPriorityReports();
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.reportType IN ('HARASSMENT', 'HATE_SPEECH', 'THREAT') AND cr.status = 'PENDING' ORDER BY cr.createdAt ASC")
    Page<ContentReport> findHighPriorityReports(Pageable pageable);
    
    // ===== ESCALACIONES =====
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.createdAt < :threshold AND cr.status = 'PENDING'")
    List<ContentReport> findOldPendingReports(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT cr FROM ContentReport cr WHERE cr.contentId IN (SELECT cr2.contentId FROM ContentReport cr2 WHERE cr2.contentType = cr.contentType GROUP BY cr2.contentId HAVING COUNT(cr2) >= :threshold)")
    List<ContentReport> findReportsOnContentWithManyReports(@Param("threshold") Integer threshold);
    
    // ===== VALIDACIONES =====
    
    @Query("SELECT COUNT(cr) > 0 FROM ContentReport cr WHERE cr.reporterId = :reporterId AND cr.contentId = :contentId AND cr.contentType = :contentType")
    boolean hasUserReportedContent(@Param("reporterId") Long reporterId, 
                                  @Param("contentId") Long contentId, 
                                  @Param("contentType") ReportedContentType contentType);
    
    @Query("SELECT COUNT(cr) FROM ContentReport cr WHERE cr.reporterId = :reporterId AND cr.createdAt >= :since")
    Long countRecentReportsByUser(@Param("reporterId") Long reporterId, @Param("since") LocalDateTime since);
    
    // ===== MÉTRICAS DE RENDIMIENTO =====
    
    @Query("SELECT AVG(FUNCTION('TIMESTAMPDIFF', HOUR, cr.createdAt, cr.reviewedAt)) FROM ContentReport cr WHERE cr.reviewedAt IS NOT NULL")
    Double getAverageReviewTime();
    
    @Query("SELECT cr.reportType, AVG(FUNCTION('TIMESTAMPDIFF', HOUR, cr.createdAt, cr.reviewedAt)) FROM ContentReport cr WHERE cr.reviewedAt IS NOT NULL GROUP BY cr.reportType")
    List<Object[]> getAverageReviewTimeByType();
    
    @Query("SELECT COUNT(cr) * 100.0 / (SELECT COUNT(cr2) FROM ContentReport cr2 WHERE cr2.createdAt >= :since) FROM ContentReport cr WHERE cr.status = 'CONFIRMED' AND cr.createdAt >= :since")
    Double getConfirmationRate(@Param("since") LocalDateTime since);
}
