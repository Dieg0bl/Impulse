package com.impulse.application.reto;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.domain.reto.Reto;
import com.impulse.domain.reto.RetoDTO;
import com.impulse.domain.reto.RetoMapper;
import com.impulse.domain.reto.RetoValidator;
import com.impulse.domain.reto.RetoRepositoryPort;

/**
 * Servicio de aplicación para Reto.
 * Gestiona operaciones de negocio, validaciones y auditoría.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Service
public class RetoService {
    private final RetoRepositoryPort retoRepository;
    private final RetoValidator retoValidator;
    private final AuditoriaService auditoriaService;
    private final RetoMapper retoMapper;
    // Compliance: Verificación de inyección y logging de dependencias
    // @Autowired removed: only one constructor present
    private static final String NOT_FOUND_MSG = "Reto no encontrado";
    public RetoService(RetoRepositoryPort retoRepository, RetoValidator retoValidator, AuditoriaService auditoriaService, RetoMapper retoMapper) {
        this.retoRepository = retoRepository;
        this.retoValidator = retoValidator;
        this.auditoriaService = auditoriaService;
        this.retoMapper = retoMapper;
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
    Reto reto = retoMapper.toEntity(dto);
    retoValidator.validarCreacion(dto);
    reto.setCreatedAt(java.time.LocalDateTime.now());
    retoRepository.save(reto);
    auditoriaService.registrarCreacionReto(reto.getId(), reto.getTitulo());
    return retoMapper.toDTO(reto);
    }

    /**
     * Consulta un reto por ID.
     */
    public RetoDTO obtenerReto(Long id) {
        Reto reto = retoRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(NOT_FOUND_MSG));
    return retoMapper.toDTO(reto);
    }

    /**
     * Elimina un reto (borrado lógico).
     */
    @Transactional
    public void eliminarReto(Long id) {
        Reto reto = retoRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(NOT_FOUND_MSG));
    reto.setDeletedAt(java.time.LocalDateTime.now());
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
    retoValidator.validarCreacion(dto);
    // record accessors
    reto.setTitulo(dto.titulo());
    reto.setDescripcion(dto.descripcion());
    // fechaLimite not present in RetoDTO; leave null unless domain provides it elsewhere
    reto.setFechaLimite(null);
    reto.setUpdatedAt(java.time.LocalDateTime.now());
    retoRepository.save(reto);
    auditoriaService.actualizarReto(reto.getId(), reto.getTitulo());
    return retoMapper.toDTO(reto);
    }

    /**
     * Lista todos los retos activos (no eliminados).
     * @return Lista de RetoDTO
     */
    public List<RetoDTO> listarRetos() {
        List<Reto> retos = retoRepository.findAll();
    return retos.stream()
            .filter(r -> r.getDeletedAt() == null)
            .map(r -> retoMapper.toDTO(r))
            .toList();
    }
}
