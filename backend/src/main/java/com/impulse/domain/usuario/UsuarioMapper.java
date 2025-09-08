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
        dto.setFechaNacimiento(usuario.getFechaNacimiento());
        dto.setEstado(usuario.getEstado());
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
        usuario.setEstado(dto.getEstado());
        usuario.setRoles(dto.getRoles());
        usuario.setConsentimientoAceptado(dto.isConsentimientoAceptado());
        return usuario;
    }
}
