package com.impulse.domain.monetizacion;

import com.impulse.domain.usuario.Usuario;

/**
 * Mapper para Pago <-> PagoDTO. Cumple compliance: RGPD, ISO 27001, ENS.
 */
public class PagoMapper {
    // Constructor privado para evitar instanciación
    private PagoMapper() {}
    public static PagoDTO toDTO(Pago pago) {
        if (pago == null) return null;
        PagoDTO dto = new PagoDTO();
        dto.setId(pago.getId());
        dto.setUsuarioId(pago.getUsuario() != null ? pago.getUsuario().getId() : null);
        dto.setCantidad(pago.getCantidad());
        dto.setMoneda(pago.getMoneda());
        dto.setEstado(pago.getEstado());
        dto.setMetodo(pago.getMetodo());
        dto.setReferencia(pago.getReferencia());
        dto.setCreatedAt(pago.getCreatedAt());
        dto.setUpdatedAt(pago.getUpdatedAt());
        dto.setDeletedAt(pago.getDeletedAt());
        dto.setCreatedBy(pago.getCreatedBy());
        dto.setUpdatedBy(pago.getUpdatedBy());
        return dto;
    }
    // Si se requiere: método fromDTO(PagoDTO dto)
    /**
     * Convierte un PagoDTO en Pago (sin cargar usuario, solo setea el id del usuario).
     * Cumple compliance: RGPD, ISO 27001, ENS.
     */
    public static Pago toEntity(PagoDTO dto) {
        if (dto == null) return null;
        Pago pago = new Pago();
        pago.setId(dto.getId());
        // Usuario: solo setea el id, la relación debe resolverse en el servicio si es necesario
        if (dto.getUsuarioId() != null) {
            Usuario usuario = new Usuario();
            usuario.setId(dto.getUsuarioId());
            pago.setUsuario(usuario);
        }
        pago.setCantidad(dto.getCantidad());
        pago.setMoneda(dto.getMoneda());
        pago.setEstado(dto.getEstado());
        pago.setMetodo(dto.getMetodo());
        pago.setReferencia(dto.getReferencia());
        pago.setCreatedAt(dto.getCreatedAt());
        pago.setUpdatedAt(dto.getUpdatedAt());
        pago.setDeletedAt(dto.getDeletedAt());
        pago.setCreatedBy(dto.getCreatedBy());
        pago.setUpdatedBy(dto.getUpdatedBy());
        return pago;
    }
}
