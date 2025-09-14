package com.impulse.domain.enums;

/**
 * Categorías de retos disponibles
 */
public enum ChallengeCategory {
    FITNESS("Fitness y Ejercicio"),
    HEALTH("Salud y Bienestar"),
    EDUCATION("Educación y Aprendizaje"),
    FINANCE("Finanzas Personales"),
    PRODUCTIVITY("Productividad"),
    CREATIVITY("Creatividad y Arte"),
    SOCIAL("Social y Comunidad"),
    ENVIRONMENT("Medio Ambiente"),
    TECHNOLOGY("Tecnología"),
    LIFESTYLE("Estilo de Vida"),
    CAREER("Carrera Profesional"),
    MINDFULNESS("Mindfulness y Meditación"),
    NUTRITION("Nutrición"),
    HOBBY("Hobbies y Pasatiempos"),
    VOLUNTEERING("Voluntariado"),
    OTHER("Otros");

    private final String descripcion;

    ChallengeCategory(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
