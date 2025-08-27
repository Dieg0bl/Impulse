
package com.impulse.api;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.impulse.domain.notificacion.Notificacion;
import com.impulse.application.ports.NotificacionPort;

@RestController("notificacionesControllerTop")
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionPort notificacionRepository;

    public NotificacionController(NotificacionPort notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<Notificacion>> getNotificaciones() {
        List<Notificacion> notificaciones = notificacionRepository.findAll();
        return ResponseEntity.ok(notificaciones);
    }

    @PostMapping("/marcar-leida/{id}")
    public ResponseEntity<Notificacion> marcarLeida(@PathVariable Long id) {
        return notificacionRepository.findById(id)
                .map(n -> {
                    n.setEnviado("ENVIADO");
                    n.setUpdatedAt(LocalDateTime.now());
                    notificacionRepository.save(n);
                    return ResponseEntity.ok(n);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Long id) {
        if (!notificacionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        notificacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
