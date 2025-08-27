package com.impulse.api;

import com.impulse.application.evidencia.EvidenciaService;
import com.impulse.domain.evidencia.EvidenciaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Controller único para manejo de evidencias.
 * Expone la ruta canónica `/api/evidencias`.
 */
@RestController
@RequestMapping("/api/evidencias")
public class EvidenciaController {

    private final EvidenciaService evidenciaService;

    public EvidenciaController(EvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @GetMapping("")
    public ResponseEntity<List<EvidenciaDTO>> getEvidencias() {
        List<EvidenciaDTO> evidencias = evidenciaService.listarEvidencias();
        return ResponseEntity.ok(evidencias);
    }

    @PostMapping("")
    public ResponseEntity<Object> crearEvidencia(
            @RequestParam("retoId") Long retoId,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("tipoEvidencia") String tipoEvidencia,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            // Aquí se debería guardar el archivo y obtener la URL real
            String url = (file != null) ? "/uploads/" + file.getOriginalFilename() : null;
            EvidenciaDTO dto = new EvidenciaDTO(
                    null, retoId, usuarioId, tipoEvidencia, null, descripcion, url, null, "PENDIENTE",
                    Instant.now(), null, null, null, null, null, null, null, null
            );
            EvidenciaDTO creada = evidenciaService.crearEvidencia(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<Map<String, Object>> reportEvidencia(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String motivo = body.getOrDefault("motivo", "");
        org.slf4j.LoggerFactory.getLogger(EvidenciaController.class).info("Evidencia {} reportada por motivo: {}", id, motivo);
        return ResponseEntity.ok(Map.of("id", id, "motivo", motivo, "status", "reportado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEvidencia(@PathVariable Long id) {
        try {
            EvidenciaDTO evidencia = evidenciaService.obtenerEvidencia(id);
            return ResponseEntity.ok(evidencia);
        } catch (com.impulse.common.exceptions.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarEvidencia(@PathVariable Long id) {
        try {
            evidenciaService.eliminarEvidencia(id);
            return ResponseEntity.noContent().build();
        } catch (com.impulse.common.exceptions.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
