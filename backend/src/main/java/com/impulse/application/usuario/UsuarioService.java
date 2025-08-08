package com.impulse.application.usuario;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.application.auditoria.AuditoriaService;
import com.impulse.common.exceptions.NotFoundException;
import com.impulse.domain.usuario.Usuario;
import com.impulse.domain.usuario.UsuarioDTO;
import com.impulse.domain.usuario.UsuarioMapper;
import com.impulse.domain.usuario.UsuarioValidator;
import com.impulse.infrastructure.usuario.UsuarioRepository;


/**
 * Servicio de aplicación para Usuario.
 * Gestiona operaciones de negocio, validaciones y auditoría.
 */
@Service
public class UsuarioService {
    private static final String USUARIO_NO_ENCONTRADO = "Usuario no encontrado";
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, AuditoriaService auditoriaService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un nuevo usuario validando unicidad y consentimiento.
     * @param dto DTO de usuario
     * @return UsuarioDTO creado
     */
    @Transactional
    public UsuarioDTO crearUsuario(UsuarioDTO dto) {
        UsuarioValidator.validar(dto);
        if (usuarioRepository.findByEmail(dto.getEmail()) != null) {
            throw new com.impulse.common.exceptions.ConflictException("El email ya está registrado.");
        }
        if (!dto.isConsentimientoAceptado()) {
            throw new com.impulse.common.exceptions.BadRequestException("Consentimiento obligatorio no aceptado.");
        }
        Usuario usuario = UsuarioMapper.toEntity(dto);
        usuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Cifrado real con bcrypt
        usuario.setCreatedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);
        auditoriaService.registrarCreacionUsuario(usuario.getId(), usuario.getEmail());
        return UsuarioMapper.toDTO(usuario);
    }

    /**
     * Consulta un usuario por ID.
     */
    public UsuarioDTO obtenerUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException(USUARIO_NO_ENCONTRADO));
        return UsuarioMapper.toDTO(usuario);
    }

    /**
     * Elimina un usuario (borrado lógico).
     */
    @Transactional
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException(USUARIO_NO_ENCONTRADO));
        usuario.setDeletedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);
        auditoriaService.registrarEliminacionUsuario(usuario.getId(), usuario.getEmail());
    }

    /**
     * Actualiza un usuario existente validando reglas de negocio y consentimiento.
     * @param id ID del usuario a actualizar
     * @param dto DTO con los datos a actualizar
     * @return UsuarioDTO actualizado
     */
    @Transactional
    public UsuarioDTO actualizarUsuario(Long id, UsuarioDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(USUARIO_NO_ENCONTRADO));
        UsuarioValidator.validar(dto);
        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.findByEmail(dto.getEmail()) != null) {
            throw new com.impulse.common.exceptions.ConflictException("El email ya está registrado.");
        }
        if (!dto.isConsentimientoAceptado()) {
            throw new com.impulse.common.exceptions.BadRequestException("Consentimiento obligatorio no aceptado.");
        }
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword())); // Cifrado real con bcrypt
        usuario.setConsentimientoAceptado(dto.isConsentimientoAceptado());
        usuario.setUpdatedAt(LocalDateTime.now());
        usuarioRepository.save(usuario);
        auditoriaService.actualizarUsuario(usuario.getId(), usuario.getEmail());
        return UsuarioMapper.toDTO(usuario);
    }

    /**
     * Lista todos los usuarios activos (no eliminados).
     * @return Lista de UsuarioDTO
     */
    public List<UsuarioDTO> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
            .filter(u -> u.getDeletedAt() == null)
            .map(UsuarioMapper::toDTO)
            .toList();
    }
}
