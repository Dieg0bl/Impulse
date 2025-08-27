package com.impulse.api;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.impulse.application.evidencia.EvidenciaService;
import com.impulse.domain.evidencia.EvidenciaDTO;

@RestController
@RequestMapping("/api/evidencias")
public class EvidenciasController {

    private final EvidenciaService evidenciaService;

    public EvidenciasController(EvidenciaService evidenciaService) {
        this.evidenciaService = evidenciaService;
    }

    @GetMapping
    public ResponseEntity<List<EvidenciaDTO>> listar() {
        List<EvidenciaDTO> lista = evidenciaService.listar();
        return ResponseEntity.ok(lista);
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<EvidenciaDTO> subir(
            @RequestParam("file") MultipartFile file,
            @RequestParam("retoId") Long retoId,
            @RequestParam(value = "comentario", required = false) String comentario) throws IOException {
        EvidenciaDTO dto = evidenciaService.subir(file, retoId, comentario);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        evidenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
