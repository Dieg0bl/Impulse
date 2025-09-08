package com.impulse.application.evidencia;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.domain.evidencia.Evidencia;
import com.impulse.domain.evidencia.EvidenciaDTO;
import com.impulse.domain.evidencia.EvidenciaMapper;
import com.impulse.domain.evidencia.EvidenciaValidator;
import com.impulse.infrastructure.evidencia.EvidenciaRepository;

/**
 * Servicio de aplicación para Evidencia.
 * Gestiona operaciones de negocio, validaciones y auditoría.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Service
public class EvidenciaService {
    private static final String EVIDENCIA_NO_ENCONTRADA = "Evidencia no encontrada";
    private final EvidenciaRepository evidenciaRepository;
    private final AuditoriaService auditoriaService;

    public EvidenciaService(EvidenciaRepository evidenciaRepository, AuditoriaService auditoriaService) {
        this.evidenciaRepository = evidenciaRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Crea una nueva evidencia validando reglas de negocio.
     * @param dto DTO de evidencia
     * @return EvidenciaDTO creada
     */
    @Transactional
    public EvidenciaDTO crearEvidencia(EvidenciaDTO dto) {
        Evidencia evidencia = EvidenciaMapper.toEntity(dto);
        java.util.Set<jakarta.validation.ConstraintViolation<Evidencia>> violations = EvidenciaValidator.validate(evidencia);
        if (!violations.isEmpty()) {
            throw new com.impulse.common.exceptions.BadRequestException("Datos de evidencia inválidos: " + violations.iterator().next().getMessage());
        }
        evidencia.setCreatedAt(LocalDateTime.now());
        evidenciaRepository.save(evidencia);
        auditoriaService.registrarCreacionEvidencia(evidencia.getId());
        return EvidenciaMapper.toDTO(evidencia);
    }

    /**
     * Consulta una evidencia por ID.
     */
    public EvidenciaDTO obtenerEvidencia(Long id) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(EVIDENCIA_NO_ENCONTRADA));
        return EvidenciaMapper.toDTO(evidencia);
    }

    /**
     * Elimina una evidencia (borrado lógico).
     */
    @Transactional
    public void eliminarEvidencia(Long id) {
        Evidencia evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(EVIDENCIA_NO_ENCONTRADA));
        evidencia.setDeletedAt(LocalDateTime.now());
        evidenciaRepository.save(evidencia);
        auditoriaService.registrarEliminacionEvidencia(evidencia.getId());
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
        Evidencia nueva = EvidenciaMapper.toEntity(dto);
        java.util.Set<jakarta.validation.ConstraintViolation<Evidencia>> violations = EvidenciaValidator.validate(nueva);
        if (!violations.isEmpty()) {
            throw new com.impulse.common.exceptions.BadRequestException("Datos de evidencia inválidos: " + violations.iterator().next().getMessage());
        }
        evidencia.setTipo(dto.getTipo());
        evidencia.setUrl(dto.getUrl());
        evidencia.setComentario(dto.getComentario());
        evidencia.setUpdatedAt(LocalDateTime.now());
        evidenciaRepository.save(evidencia);
        auditoriaService.actualizarEvidencia(evidencia.getId());
        return EvidenciaMapper.toDTO(evidencia);
    }

    /**
     * Lista todas las evidencias activas (no eliminadas).
     * @return Lista de EvidenciaDTO
     */
    public List<EvidenciaDTO> listarEvidencias() {
        List<Evidencia> evidencias = evidenciaRepository.findAll();
        return evidencias.stream()
            .filter(e -> e.getDeletedAt() == null)
            .map(EvidenciaMapper::toDTO)
            .toList();
    }
}
