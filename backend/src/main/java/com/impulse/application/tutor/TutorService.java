package com.impulse.application.tutor;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.domain.tutor.Tutor;
import com.impulse.domain.tutor.TutorDTO;
import com.impulse.domain.tutor.TutorMapper;
import com.impulse.domain.tutor.TutorValidator;
import com.impulse.application.ports.TutorPort;

/**
 * Servicio de aplicación para Tutor.
 * Gestiona operaciones de negocio, validaciones y auditoría.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Service
public class TutorService {
    private final TutorPort tutorRepository;
    private final AuditoriaService auditoriaService;

    private static final String MSG_TUTOR_NO_ENCONTRADO = "Tutor no encontrado";

    public TutorService(TutorPort tutorRepository, AuditoriaService auditoriaService) {
        this.tutorRepository = tutorRepository;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Crea un nuevo tutor validando reglas de negocio.
     * @param dto DTO de tutor
     * @return TutorDTO creado
     */
    @Transactional
    public TutorDTO crearTutor(TutorDTO dto) {
        TutorValidator.validar(dto);
        Tutor tutor = TutorMapper.toEntity(dto);
        tutor.setCreatedAt(LocalDateTime.now());
        tutorRepository.save(tutor);
        auditoriaService.registrarCreacionTutor(tutor.getId(), tutor.getEmail());
        return TutorMapper.toDTO(tutor);
    }

    /**
     * Consulta un tutor por ID.
     */
    public TutorDTO obtenerTutor(Long id) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(MSG_TUTOR_NO_ENCONTRADO));
        return TutorMapper.toDTO(tutor);
    }

    /**
     * Elimina un tutor (borrado lógico).
     */
    @Transactional
    public void eliminarTutor(Long id) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(MSG_TUTOR_NO_ENCONTRADO));
        tutor.setDeletedAt(LocalDateTime.now());
        tutorRepository.save(tutor);
        auditoriaService.registrarEliminacionTutor(tutor.getId(), tutor.getEmail());
    }

    /**
     * Actualiza un tutor existente validando reglas de negocio.
     * @param id ID del tutor a actualizar
     * @param dto DTO con los datos a actualizar
     * @return TutorDTO actualizado
     */
    @Transactional
    public TutorDTO actualizarTutor(Long id, TutorDTO dto) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(MSG_TUTOR_NO_ENCONTRADO));
        TutorValidator.validar(dto);
        tutor.setNombre(dto.getNombre());
        tutor.setEmail(dto.getEmail());
        tutor.setUpdatedAt(LocalDateTime.now());
        tutorRepository.save(tutor);
        auditoriaService.actualizarTutor(tutor.getId(), tutor.getEmail());
        return TutorMapper.toDTO(tutor);
    }

    /**
     * Lista todos los tutores activos (no eliminados).
     * @return Lista de TutorDTO
     */
    public List<TutorDTO> listarTutores() {
        List<Tutor> tutores = tutorRepository.findAll();
        return tutores.stream()
            .filter(t -> t.getDeletedAt() == null)
            .map(TutorMapper::toDTO)
            .toList();
    }
}
