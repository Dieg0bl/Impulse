-- =============================================================
-- Vista: v_rbac_users
-- Descripción: Usuarios y sus roles RBAC
-- Cumple: Seguridad, soporte, solo-lectura
-- Autor: GitHub Copilot
-- Fecha: 2025-08-12
-- =============================================================
CREATE OR REPLACE VIEW v_rbac_users AS
SELECT u.id, u.email, r.nombre AS rol, u.estado, u.created_at
FROM Usuario u
JOIN UsuarioRol ur ON ur.idUsuario = u.id
JOIN Rol r ON r.id = ur.idRol;
