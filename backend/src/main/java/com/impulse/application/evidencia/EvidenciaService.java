package com.impulse.application.evidencia;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.domain.evidencia.Evidencia;
import com.impulse.domain.evidencia.EvidenciaDTO;
import com.impulse.domain.evidencia.EvidenciaMapper;
import com.impulse.domain.evidencia.EvidenciaValidator;
import com.impulse.domain.evidencia.EvidenciaRepositoryPort;

/**
 * Servicio de aplicación para Evidencia.
 * Gestiona operaciones de negocio, validaciones y auditoría.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Service
public class EvidenciaService {
    private static final String EVIDENCIA_NO_ENCONTRADA = "Evidencia no encontrada";
    private final EvidenciaRepositoryPort evidenciaRepository;
    private final AuditoriaService auditoriaService;
    private final EvidenciaMapper evidenciaMapper;
    private final EvidenciaValidator evidenciaValidator;

    public EvidenciaService(EvidenciaRepositoryPort evidenciaRepository, AuditoriaService auditoriaService, EvidenciaMapper evidenciaMapper, EvidenciaValidator evidenciaValidator) {
        this.evidenciaRepository = evidenciaRepository;
        this.auditoriaService = auditoriaService;
        this.evidenciaMapper = evidenciaMapper;
        this.evidenciaValidator = evidenciaValidator;
    }

    /**
     * Crea una nueva evidencia validando reglas de negocio.
     * @param dto DTO de evidencia
     * @return EvidenciaDTO creada
     */
    @Transactional
    public EvidenciaDTO crearEvidencia(EvidenciaDTO dto) {
    return crearEvidenciaImpl(dto);
    }

    /**
     * Consulta una evidencia por ID.
     */
    public EvidenciaDTO obtenerEvidencia(Long id) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(EVIDENCIA_NO_ENCONTRADA));
    return evidenciaMapper.toDTO(evidencia);
    }

    /**
     * Elimina una evidencia (borrado lógico).
     */
    @Transactional
    public void eliminarEvidencia(Long id) {
    eliminarEvidenciaImpl(id);
    }

    /**
     * Actualiza una evidencia existente validando reglas de negocio.
     * @param id ID de la evidencia a actualizar
     * @param dto DTO con los datos a actualizar
     * @return EvidenciaDTO actualizada
     */
    @Transactional
    public EvidenciaDTO actualizarEvidencia(Long id, EvidenciaDTO dto) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(EVIDENCIA_NO_ENCONTRADA));
        // Validación estricta
        if (evidenciaValidator != null) {
            evidenciaValidator.validarNueva(dto);
        }
        // DTO is a record: use accessors
        evidencia.setTipo(dto.tipoEvidencia());
        evidencia.setUrl(dto.downloadUrl());
        evidencia.setComentario(dto.descripcion());
        evidencia.setUpdatedAt(java.time.LocalDateTime.now());
        evidenciaRepository.save(evidencia);
    auditoriaService.actualizarEvidencia(evidencia.getId());
    return evidenciaMapper.toDTO(evidencia);
    }

    /**
     * Lista todas las evidencias activas (no eliminadas).
     * @return Lista de EvidenciaDTO
     */
    public List<EvidenciaDTO> listarEvidencias() {
        List<Evidencia> evidencias = evidenciaRepository.findAll();
        return evidencias.stream()
            .filter(e -> e.getDeletedAt() == null)
            .map(evidenciaMapper::toDTO)
            .toList();
    }

    /* Adapter methods used by controllers expecting different method names */
    public List<EvidenciaDTO> listar() {
        return listarEvidencias();
    }

    @Transactional
    public EvidenciaDTO subir(org.springframework.web.multipart.MultipartFile file, Long retoId, String comentario) {
        // Minimal adapter: map incoming multipart to DTO and reuse crearEvidencia
        EvidenciaDTO dto = new EvidenciaDTO(
            null,
            retoId,
            null,
            file != null ? file.getOriginalFilename() : "file",
            null,
            comentario,
            file != null ? "/uploads/" + file.getOriginalFilename() : null,
            null,
            "PENDIENTE",
            java.time.Instant.now(),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    return crearEvidencia(dto);
    }

    @Transactional
    public void eliminar(Long id) {
        eliminarEvidencia(id);
    }

    // Private implementation helpers to satisfy static analysis that transactional methods
    // should be the ones with @Transactional and to avoid calling them via 'this' in some analyzers.
    private EvidenciaDTO crearEvidenciaImpl(EvidenciaDTO dto) {
        Evidencia evidencia = evidenciaMapper.toEntity(dto);
        if (evidenciaValidator != null) {
            evidenciaValidator.validarNueva(dto);
        }
        evidencia.setCreatedAt(java.time.LocalDateTime.now());
        evidenciaRepository.save(evidencia);
        auditoriaService.registrarCreacionEvidencia(evidencia.getId());
        return evidenciaMapper.toDTO(evidencia);
    }

    private void eliminarEvidenciaImpl(Long id) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(EVIDENCIA_NO_ENCONTRADA));
        evidencia.setDeletedAt(java.time.LocalDateTime.now());
        evidenciaRepository.save(evidencia);
        auditoriaService.registrarEliminacionEvidencia(evidencia.getId());
    }
}
