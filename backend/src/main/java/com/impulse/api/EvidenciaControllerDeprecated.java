package com.impulse.api;

import com.impulse.application.evidencia.EvidenciaService;
import com.impulse.domain.evidencia.EvidenciaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@Deprecated(since = "1.0", forRemoval = false)
@RestController("evidenciasControllerTopDeprecated")
@RequestMapping("/api/_deprecated_evidencias")
public class EvidenciaControllerDeprecated {
    private final EvidenciaService evidenciaService;

    /**
     * @deprecated This controller is kept for backwards compatibility during migration.
     */
    @Autowired
    public EvidenciaControllerDeprecated(EvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @GetMapping("")
    public ResponseEntity<List<EvidenciaDTO>> getEvidencias() {
        List<EvidenciaDTO> evidencias = evidenciaService.listarEvidencias();
        return ResponseEntity.ok(evidencias);
    }

    @PostMapping("")
    public ResponseEntity<?> crearEvidencia(
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
                java.time.Instant.now(), null, null, null, null, null, null, null, null
            );
            EvidenciaDTO creada = evidenciaService.crearEvidencia(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/report")
    public ResponseEntity<?> reportEvidencia(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        // Aquí se podría registrar el reporte en la auditoría o en una tabla de reportes
        String motivo = body.getOrDefault("motivo", "");
        // Simulación: solo loguear el reporte
    org.slf4j.LoggerFactory.getLogger(EvidenciaControllerDeprecated.class).info("Evidencia {} reportada por motivo: {}", id, motivo);
        return ResponseEntity.ok(java.util.Map.of("id", id, "motivo", motivo, "status", "reportado"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvidencia(@PathVariable Long id) {
        try {
            EvidenciaDTO evidencia = evidenciaService.obtenerEvidencia(id);
            return ResponseEntity.ok(evidencia);
        } catch (com.impulse.common.exceptions.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEvidencia(@PathVariable Long id) {
        try {
            evidenciaService.eliminarEvidencia(id);
            return ResponseEntity.noContent().build();
        } catch (com.impulse.common.exceptions.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
