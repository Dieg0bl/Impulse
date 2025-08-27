package com.impulse.application.monetizacion;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.impulse.domain.monetizacion.Pago;
import com.impulse.domain.monetizacion.PagoDTO;
import com.impulse.domain.monetizacion.PagoMapper;
import com.impulse.domain.monetizacion.PagoValidator;
import com.impulse.application.ports.PagoPort;

import jakarta.validation.ConstraintViolation;

/**
 * Servicio de aplicación para Pago (Monetizacion).
 * Cumple compliance: RGPD, ISO 27001, ENS.
 */
@Service
public class PagoService {
    private static final String PAGO_NO_ENCONTRADO = "Pago no encontrado";
    private final PagoPort pagoRepository;

    public PagoService(PagoPort pagoRepository) {
        this.pagoRepository = pagoRepository;
    }

    @Transactional(readOnly = true)
    public PagoDTO obtenerPago(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(PAGO_NO_ENCONTRADO));
        return PagoMapper.toDTO(pago);
    }

    @Transactional
    public PagoDTO crearPago(PagoDTO dto) {
        // Validación básica
        Pago pago = PagoMapper.toEntity(dto);
        Set<ConstraintViolation<Pago>> violations = PagoValidator.validate(pago);
        if (!violations.isEmpty()) {
            throw new com.impulse.common.exceptions.BadRequestException("Datos de pago inválidos: " + violations.iterator().next().getMessage());
        }
        Pago creado = pagoRepository.save(pago);
        return PagoMapper.toDTO(creado);
    }

    @Transactional
    public PagoDTO actualizarPago(Long id, PagoDTO dto) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(PAGO_NO_ENCONTRADO));
        // Actualizar campos permitidos
        pago.setCantidad(dto.getCantidad());
        pago.setMoneda(dto.getMoneda());
        pago.setEstado(dto.getEstado());
        pago.setMetodo(dto.getMetodo());
        pago.setReferencia(dto.getReferencia());
        pago.setUpdatedAt(java.time.LocalDateTime.now());
        Pago actualizado = pagoRepository.save(pago);
        return PagoMapper.toDTO(actualizado);
    }

    @Transactional
    public void eliminarPago(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new com.impulse.common.exceptions.NotFoundException(PAGO_NO_ENCONTRADO));
        pago.setDeletedAt(java.time.LocalDateTime.now());
        pagoRepository.save(pago);
    }
}
