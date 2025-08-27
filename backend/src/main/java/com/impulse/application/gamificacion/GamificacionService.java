package com.impulse.application.gamificacion;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.common.exceptions.BadRequestException;
import com.impulse.common.exceptions.NotFoundException;
import com.impulse.domain.gamificacion.Gamificacion;
import com.impulse.domain.gamificacion.GamificacionDTO;
import com.impulse.domain.gamificacion.GamificacionMapper;
import com.impulse.domain.gamificacion.GamificacionValidator;
import com.impulse.application.ports.GamificacionPort;

/**
 * Servicio de aplicación para Gamificación.
 * Gestiona operaciones de negocio, validaciones y auditoría.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Service
public class GamificacionService {
    private static final String NOT_FOUND_MSG = "Gamificación no encontrada";
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GamificacionService.class);
    private final GamificacionPort gamificacionRepository;
    private final GamificacionValidator gamificacionValidator;
    private final AuditoriaService auditoriaService;
    // Compliance: Verificación de inyección y logging de dependencias
    public GamificacionService(GamificacionPort gamificacionRepository, GamificacionValidator gamificacionValidator, AuditoriaService auditoriaService) {
        this.gamificacionRepository = gamificacionRepository;
        this.gamificacionValidator = gamificacionValidator;
        this.auditoriaService = auditoriaService;
        assert this.gamificacionRepository != null : "GamificacionRepository no inyectado";
        assert this.gamificacionValidator != null : "GamificacionValidator no inyectado";
        assert this.auditoriaService != null : "AuditoriaService no inyectado";
        if (logger.isInfoEnabled()) {
            logger.info("GamificacionService inicializado correctamente");
        }
    }

    /**
     * Crea una nueva gamificación validando reglas de negocio.
     * @param dto DTO de gamificación
     * @return GamificacionDTO creada
     */
    @Transactional
    public GamificacionDTO crearGamificacion(GamificacionDTO dto) {
        Gamificacion gamificacion = GamificacionMapper.toEntity(dto);
        java.util.Set<jakarta.validation.ConstraintViolation<Gamificacion>> violations = gamificacionValidator.validate(gamificacion);
        if (!violations.isEmpty()) {
            throw new BadRequestException("Datos de gamificación inválidos: " + violations.iterator().next().getMessage());
        }
        gamificacion.setCreatedAt(LocalDateTime.now());
        gamificacionRepository.save(gamificacion);
        auditoriaService.registrarCreacionGamificacion(gamificacion.getId());
        return GamificacionMapper.toDTO(gamificacion);
    }

    /**
     * Consulta una gamificación por ID.
     */
    public GamificacionDTO obtenerGamificacion(Long id) {
        Gamificacion gamificacion = gamificacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MSG));
        return GamificacionMapper.toDTO(gamificacion);
    }

    /**
     * Elimina una gamificación (borrado lógico).
     */
    @Transactional
    public void eliminarGamificacion(Long id) {
        Gamificacion gamificacion = gamificacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MSG));
        gamificacion.setDeletedAt(LocalDateTime.now());
        gamificacionRepository.save(gamificacion);
        auditoriaService.registrarEliminacionGamificacion(gamificacion.getId());
    }

    /**
     * Actualiza una gamificación existente validando reglas de negocio.
     * @param id ID de la gamificación a actualizar
     * @param dto DTO con los datos a actualizar
     * @return GamificacionDTO actualizada
     */
    @Transactional
    public GamificacionDTO actualizarGamificacion(Long id, GamificacionDTO dto) {
        Gamificacion gamificacion = gamificacionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MSG));
        Gamificacion nueva = GamificacionMapper.toEntity(dto);
        java.util.Set<jakarta.validation.ConstraintViolation<Gamificacion>> violations = gamificacionValidator.validate(nueva);
        if (!violations.isEmpty()) {
            throw new BadRequestException("Datos de gamificación inválidos: " + violations.iterator().next().getMessage());
        }
        gamificacion.setTipo(dto.getTipo());
        gamificacion.setPuntos(dto.getPuntos());
        gamificacion.setCanal(dto.getCanal());
        gamificacion.setUpdatedAt(LocalDateTime.now());
        gamificacionRepository.save(gamificacion);
        auditoriaService.actualizarGamificacion(gamificacion.getId());
        return GamificacionMapper.toDTO(gamificacion);
    }

    /**
     * Lista todas las gamificaciones activas (no eliminadas).
     * @return Lista de GamificacionDTO
     */
    public List<GamificacionDTO> listarGamificaciones() {
        List<Gamificacion> gamificaciones = gamificacionRepository.findAll();
        return gamificaciones.stream()
            .filter(g -> g.getDeletedAt() == null)
            .map(GamificacionMapper::toDTO)
            .toList();
    }
}
