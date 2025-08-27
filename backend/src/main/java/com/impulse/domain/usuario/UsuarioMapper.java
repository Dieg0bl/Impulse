package com.impulse.domain.usuario;

/**
 * Mapper para convertir entre Usuario y UsuarioDTO.
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class UsuarioMapper {

    private UsuarioMapper() {
        // Utilidad, no instanciable
    }

    public static UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setPassword(null); // Nunca exponer password real
    // fechaNacimiento ahora es Instant en entidad y DTO
    dto.setFechaNacimiento(usuario.getFechaNacimiento());
    // estado en DTO es String
    dto.setEstado(usuario.getEstado() != null ? usuario.getEstado().name() : null);
    dto.setRoles(usuario.getRoles());
        dto.setConsentimientoAceptado(usuario.isConsentimientoAceptado());
        return dto;
    }

    public static Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) return null;
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setEmail(dto.getEmail());
        usuario.setNombre(dto.getNombre());
        usuario.setPassword(dto.getPassword());
        usuario.setFechaNacimiento(dto.getFechaNacimiento());
        // convertir estado String -> enum
        if (dto.getEstado() != null) {
            try { usuario.setEstado(Usuario.Estado.valueOf(dto.getEstado())); } catch (IllegalArgumentException ex) { usuario.setEstado(null); }
        }
        usuario.setRoles(dto.getRoles());
        usuario.setConsentimientoAceptado(dto.isConsentimientoAceptado());
        return usuario;
    }
}
