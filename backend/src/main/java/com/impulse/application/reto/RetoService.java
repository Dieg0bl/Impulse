package com.impulse.application.reto;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.domain.reto.Reto;
import com.impulse.domain.reto.RetoDTO;
import com.impulse.domain.reto.RetoMapper;
import com.impulse.domain.reto.RetoValidator;
import com.impulse.infrastructure.reto.RetoRepository;

/**
 * Servicio de aplicación para Reto.
 * Gestiona operaciones de negocio, validaciones y auditoría.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Service
public class RetoService {
    private final RetoRepository retoRepository;
    private final RetoValidator retoValidator;
    private final AuditoriaService auditoriaService;
    private final com.impulse.monetization.PlanService planService; // monetization enforcement
    // Compliance: Verificación de inyección y logging de dependencias
    // @Autowired removed: only one constructor present
    private static final String NOT_FOUND_MSG = "Reto no encontrado";
    public RetoService(RetoRepository retoRepository, RetoValidator retoValidator, AuditoriaService auditoriaService, com.impulse.monetization.PlanService planService) {
        this.retoRepository = retoRepository;
        this.retoValidator = retoValidator;
        this.auditoriaService = auditoriaService;
        this.planService = planService;
        assert this.retoRepository != null : "RetoRepository no inyectado";
        assert this.retoValidator != null : "RetoValidator no inyectado";
        assert this.auditoriaService != null : "AuditoriaService no inyectado";
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RetoService.class);
        if (logger.isInfoEnabled()) {
            logger.info("RetoService inicializado correctamente");
        }
    }

    /**
     * Crea un nuevo reto validando reglas de negocio.
     * @param dto DTO de reto
     * @return RetoDTO creado
     */
    @Transactional
    public RetoDTO crearReto(RetoDTO dto) {
        // Monetization limit check (soft enforcement)
        if(dto.getUsuarioId()!=null && !planService.canCreateReto(dto.getUsuarioId())){
            throw new com.impulse.common.exceptions.BadRequestException("Limite de retos alcanzado para tu plan");
        }
        Reto reto = RetoMapper.toEntity(dto);
        java.util.Set<jakarta.validation.ConstraintViolation<Reto>> violations = retoValidator.validate(reto);
        if (!violations.isEmpty()) {
            throw new com.impulse.common.exceptions.BadRequestException("Datos de reto inválidos: " + violations.iterator().next().getMessage());
        }
        reto.setCreatedAt(LocalDateTime.now());
        retoRepository.save(reto);
        auditoriaService.registrarCreacionReto(reto.getId(), reto.getTitulo());
        return RetoMapper.toDTO(reto);
    }

    /**
     * Consulta un reto por ID.
     */
    public RetoDTO obtenerReto(Long id) {
        Reto reto = retoRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(NOT_FOUND_MSG));
        return RetoMapper.toDTO(reto);
    }

    /**
     * Elimina un reto (borrado lógico).
     */
    @Transactional
    public void eliminarReto(Long id) {
        Reto reto = retoRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(NOT_FOUND_MSG));
        reto.setDeletedAt(LocalDateTime.now());
        retoRepository.save(reto);
        auditoriaService.registrarEliminacionReto(reto.getId(), reto.getTitulo());
    }

    /**
     * Actualiza un reto existente validando reglas de negocio.
     * @param id ID del reto a actualizar
     * @param dto DTO con los datos a actualizar
     * @return RetoDTO actualizado
     */
    @Transactional
    public RetoDTO actualizarReto(Long id, RetoDTO dto) {
        Reto reto = retoRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(NOT_FOUND_MSG));
        Reto nuevo = RetoMapper.toEntity(dto);
        java.util.Set<jakarta.validation.ConstraintViolation<Reto>> violations = retoValidator.validate(nuevo);
        if (!violations.isEmpty()) {
            throw new com.impulse.common.exceptions.BadRequestException("Datos de reto inválidos: " + violations.iterator().next().getMessage());
        }
        reto.setTitulo(dto.getTitulo());
        reto.setDescripcion(dto.getDescripcion());
        reto.setFechaLimite(dto.getFechaLimite());
        reto.setUpdatedAt(LocalDateTime.now());
        retoRepository.save(reto);
        auditoriaService.actualizarReto(reto.getId(), reto.getTitulo());
        return RetoMapper.toDTO(reto);
    }

    /**
     * Lista todos los retos activos (no eliminados).
     * @return Lista de RetoDTO
     */
    public List<RetoDTO> listarRetos() {
        List<Reto> retos = retoRepository.findAll();
        return retos.stream()
            .filter(r -> r.getDeletedAt() == null)
            .map(RetoMapper::toDTO)
            .toList();
    }
}
