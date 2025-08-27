package com.impulse.api;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.impulse.application.usuario.UsuarioService;
import com.impulse.application.reto.RetoService;
import com.impulse.application.gamificacion.GamificacionService;
import com.impulse.domain.usuario.UsuarioDTO;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {
    private final UsuarioService usuarioService;
    private final RetoService retoService;
    private final GamificacionService gamificacionService;

    public EstadisticaController(UsuarioService usuarioService, RetoService retoService, GamificacionService gamificacionService) {
        this.usuarioService = usuarioService;
        this.retoService = retoService;
        this.gamificacionService = gamificacionService;
    }

    @GetMapping("")
    public ResponseEntity<?> getEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        var usuarios = usuarioService.listarUsuarios();
        var retos = retoService.listarRetos();
        var gamificaciones = gamificacionService.listarGamificaciones();

        int totalUsuarios = usuarios.size();
        int totalRetos = retos.size();
        int totalPuntos = gamificaciones.stream().mapToInt(g -> g.getPuntos() != null ? g.getPuntos() : 0).sum();
        int totalBadges = (int) gamificaciones.stream().filter(g -> "BADGE".equalsIgnoreCase(g.getTipo())).count();
        int maxRacha = usuarios.stream().map(u -> u.getEstadisticas() != null ? u.getEstadisticas().getRacha() : 0).max(Integer::compareTo).orElse(0);

        stats.put("totalUsuarios", totalUsuarios);
        stats.put("totalRetos", totalRetos);
        stats.put("totalPuntos", totalPuntos);
        stats.put("totalBadges", totalBadges);
        stats.put("maxRacha", maxRacha);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<?> getEstadisticasUsuario(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.obtenerUsuario(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> stats = new HashMap<>();
        var retos = retoService.listarRetos();
        var gamificaciones = gamificacionService.listarGamificaciones();

        int retosCompletados = retos.stream().filter(r -> r.idCreador() != null && r.idCreador().equals(id) && "COMPLETADO".equalsIgnoreCase(r.estado())).toList().size();
        int puntosTotales = gamificaciones.stream().filter(g -> g.getUsuarioId() != null && g.getUsuarioId().equals(id)).mapToInt(g -> g.getPuntos() != null ? g.getPuntos() : 0).sum();
        int racha = usuario.getEstadisticas() != null ? usuario.getEstadisticas().getRacha() : 0;
        String[] badges = gamificaciones.stream().filter(g -> g.getUsuarioId() != null && g.getUsuarioId().equals(id) && "BADGE".equalsIgnoreCase(g.getTipo())).map(g -> g.getTipo()).toArray(String[]::new);

        stats.put("retosCompletados", retosCompletados);
        stats.put("puntosTotales", puntosTotales);
        stats.put("badges", badges);
        stats.put("racha", racha);

        return ResponseEntity.ok(stats);
    }
}
