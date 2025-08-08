package com.impulse.application.notificacion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.domain.notificacion.Notificacion;
import com.impulse.domain.notificacion.NotificacionDTO;
import com.impulse.domain.notificacion.NotificacionMapper;
import com.impulse.domain.notificacion.NotificacionValidator;
import com.impulse.infrastructure.notificacion.NotificacionRepository;

/**
 * Servicio de aplicación para Notificación.
 * Gestiona operaciones de negocio, validaciones y auditoría.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Service
public class NotificacionService {
    private final NotificacionRepository notificacionRepository;
    private final AuditoriaService auditoriaService;

    private static final String NOTIFICACION_NO_ENCONTRADA = "Notificación no encontrada";

    public NotificacionService(NotificacionRepository notificacionRepository, AuditoriaService auditoriaService) {
        this.notificacionRepository = notificacionRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Crea una nueva notificación validando reglas de negocio.
     * @param dto DTO de notificación
     * @return NotificacionDTO creada
     */
    @Transactional
    public NotificacionDTO crearNotificacion(NotificacionDTO dto) {
        NotificacionValidator.validar(dto);
        Notificacion notificacion = NotificacionMapper.toEntity(dto);
        notificacion.setCreatedAt(LocalDateTime.now());
        notificacionRepository.save(notificacion);
        auditoriaService.registrarCreacionNotificacion(notificacion.getId());
        return NotificacionMapper.toDTO(notificacion);
    }

    /**
     * Consulta una notificación por ID.
     */
    public NotificacionDTO obtenerNotificacion(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(NOTIFICACION_NO_ENCONTRADA));
        return NotificacionMapper.toDTO(notificacion);
    }

    /**
     * Elimina una notificación (borrado lógico).
     */
    @Transactional
    public void eliminarNotificacion(Long id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(NOTIFICACION_NO_ENCONTRADA));
        notificacion.setDeletedAt(LocalDateTime.now());
        notificacionRepository.save(notificacion);
        auditoriaService.registrarEliminacionNotificacion(notificacion.getId());
    }

    /**
     * Actualiza una notificación existente validando reglas de negocio.
     * @param id ID de la notificación a actualizar
     * @param dto DTO con los datos a actualizar
     * @return NotificacionDTO actualizada
     */
    @Transactional
    public NotificacionDTO actualizarNotificacion(Long id, NotificacionDTO dto) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(NOTIFICACION_NO_ENCONTRADA));
        NotificacionValidator.validar(dto);
        notificacion.setTipo(dto.getTipo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setCanal(dto.getCanal());
        notificacion.setUpdatedAt(LocalDateTime.now());
        notificacionRepository.save(notificacion);
        auditoriaService.actualizarNotificacion(notificacion.getId());
        return NotificacionMapper.toDTO(notificacion);
    }

    /**
     * Lista todas las notificaciones activas (no eliminadas).
     * @return Lista de NotificacionDTO
     */
    public List<NotificacionDTO> listarNotificaciones() {
        List<Notificacion> notificaciones = notificacionRepository.findAll();
        return notificaciones.stream()
            .filter(n -> n.getDeletedAt() == null)
            .map(NotificacionMapper::toDTO)
            .toList();
    }
}
