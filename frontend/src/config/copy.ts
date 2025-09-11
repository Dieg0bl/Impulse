// IMPULSE LEAN - Copy Text Configuration (Specification Compliant)
// Implements exact copy text from specification requirements

export const HERO_COPY = {
  // Section 12: Copy pegable
  main: "Invierte en ti. Haz que cada meta cuente.",
  subtitle: "La plataforma de retos personales con validación comunitaria",
  
  // Beta messaging
  betaNotice: "IMPULSE está en beta abierta 90 días: sin tarjeta, sin cobros, sin renovación. Al finalizar, podrás elegir plan o seguir en Basic gratis.",
  
  // Legal footer (Section 19)
  legalFooter: "Al continuar aceptas Términos, Privacidad y Cookies. Cuando actives un plan de pago, podrás cancelar en 1 clic desde tu Portal."
};

export const EMAIL_COPY = {
  // Welcome email during beta
  welcome: {
    subject: "¡Bienvenido a IMPULSE! Tu beta de 90 días ha comenzado",
    body: "Tienes acceso gratis 90 días sin tarjeta. El {fecha_fin} podrás elegir plan o seguir en Basic gratis.",
    greeting: "¡Hola {nombre}!",
    closing: "¡Empieza a crear tus primeros retos y alcanza tus metas!"
  },
  
  // Beta ending alerts (Section 2)
  betaReminder15: {
    subject: "IMPULSE - Tu beta finaliza en 15 días",
    body: "Si quieres Pro, lo activas tú (sin sorpresas). Si no, pasas a Basic gratis.",
    cta: "Ver planes disponibles"
  },
  
  betaReminder7: {
    subject: "IMPULSE - Tu beta finaliza en 7 días", 
    body: "Si quieres Pro, lo activas tú (sin sorpresas). Si no, pasas a Basic gratis.",
    cta: "Decidir ahora"
  },
  
  betaReminder1: {
    subject: "IMPULSE - Tu beta finaliza mañana",
    body: "Si quieres Pro, lo activas tú (sin sorpresas). Si no, pasas a Basic gratis.",
    cta: "Última oportunidad"
  }
};

export const BILLING_COPY = {
  // Guarantee text (Section 3)
  guarantee: "Garantía 30 días (cuando empieces a cobrar): primera compra, no renovaciones; excluye uso intensivo manifiesto.",
  
  // Billing disabled message
  billingDisabled: "¡Próximamente! Durante la beta de 90 días, todos los usuarios tienen acceso completo sin cobros.",
  
  // Plan descriptions (specification compliant)
  plans: {
    basic: {
      name: "Basic",
      tagline: "gratis",
      description: "hasta 2 retos activos · 3 validadores/reto · texto + 1 imagen · privacidad estándar · historial básico"
    },
    pro: {
      name: "Pro", 
      tagline: "12,99 €/mes · 99 €/año",
      description: "retos/validadores ilimitados · multimedia completa · retos privados/equipo · stats avanzadas · exportación · soporte prioritario"
    },
    teams: {
      name: "Teams",
      tagline: "39,99 €/mes incluye 10; +4 €/miembro extra", 
      description: "dashboard equipo · colaborativos · competiciones · reportes · roles y onboarding"
    },
    coach: {
      name: "Coach",
      tagline: "personalizado",
      description: "marketplace, 1-a-1, marca blanca, analíticas. (Desactivado hasta go-live fiscal)"
    }
  }
};

export const DASHBOARD_COPY = {
  // Section 14: Domain functional copy
  welcome: "¡Hola {nombre}! ¿Listo para tu próximo reto?",
  
  cta: {
    newChallenge: "Nuevo reto",
    uploadProgress: "Subir progreso"
  },
  
  // Challenge status messages
  challengeStatus: {
    active: "Reto activo - {diasRestantes} días restantes",
    completed: "¡Reto completado! 🎉",
    pending: "Esperando validación",
    failed: "Reto no completado"
  },
  
  // Evidence upload
  evidenceUpload: {
    title: "Nueva Evidencia",
    selectChallenge: "Selector de reto activo",
    basicLimit: "texto + 1 imagen en Basic",
    unlimited: "multimedia completa en Pro/Teams"
  },
  
  // Validation panel
  validation: {
    title: "Validaciones Pendientes",
    approveAction: "Aprobar",
    rejectAction: "Rechazar",
    feedbackRequired: "feedback obligatorio si rechazo",
    badgeText: "pendientes"
  },
  
  // Achievement section
  achievements: {
    title: "Tus Logros",
    kpis: "completados, totales, subidas, aprobadas",
    timeline: "timeline"
  }
};

export const NAVIGATION_COPY = {
  // Main navigation
  dashboard: "Dashboard",
  challenges: "Retos", 
  evidences: "Evidencias",
  validation: "Validación",
  plans: "Planes",
  account: "Cuenta",
  
  // Account menu
  profile: "Mi Perfil",
  settings: "Configuración", 
  export: "Exportar Datos",
  delete: "Borrar Cuenta",
  logout: "Cerrar Sesión"
};

export const FORM_COPY = {
  // Challenge creation
  challenge: {
    title: "título*",
    description: "desc*", 
    deadline: "fecha límite*",
    visibility: "visibilidad",
    validators: "validadores",
    basicLimit: "límite Basic: 2 activos"
  },
  
  // Evidence submission  
  evidence: {
    title: "Título de la evidencia",
    description: "Descripción",
    media: "Subir archivo",
    basicLimit: "1 imagen en Basic",
    proUnlimited: "multimedia completa en Pro"
  },
  
  // Validation forms
  validation: {
    decision: "Decisión",
    feedback: "Comentarios",
    score: "Puntuación",
    confidence: "Nivel de confianza"
  }
};

export const MODERATION_COPY = {
  // Section 7: Moderation rules
  prohibited: {
    title: "Contenido Prohibido",
    items: [
      "sexual/desnudos",
      "odio/violencia", 
      "ilegal",
      "datos de terceros",
      "spam/fraude",
      "salud/finanzas sensibles"
    ]
  },
  
  reporting: {
    action: "Reportar in-app",
    sla: "SLA ≤72h",
    strikes: "3 strikes ⇒ suspensión",
    appeal: "apelación a impulse.legal@gmail.com"
  }
};

export const LEGAL_COPY = {
  // Section 1: Company info
  company: {
    name: "IMPULSE",
    owner: "Diego Barreiro Liste",
    address: "Oroso (A Coruña), España, 15688"
  },
  
  // Contact emails
  contacts: {
    support: "impulse.soporte@gmail.com",
    legal: "impulse.legal@gmail.com", 
    abuse: "impulse.abuse@gmail.com"
  },
  
  // Footer text (Section 19)
  footer: "IMPULSE – nombre comercial de Diego Barreiro Liste · Oroso (A Coruña), España\nSoporte: impulse.soporte@gmail.com · RGPD: impulse.legal@gmail.com · Abusos: impulse.abuse@gmail.com"
};

// Utility functions for dynamic text replacement
export const replacePlaceholders = (text: string, replacements: Record<string, string>): string => {
  return text.replace(/{(\w+)}/g, (match, key) => replacements[key] || match);
};

export const formatEmailBody = (template: string, userInfo: { nombre?: string; fecha_fin?: string }): string => {
  return replacePlaceholders(template, {
    nombre: userInfo.nombre || 'Usuario',
    fecha_fin: userInfo.fecha_fin || 'fecha a determinar'
  });
};

// Export all copy collections
export const COPY = {
  HERO_COPY,
  EMAIL_COPY,
  BILLING_COPY,
  DASHBOARD_COPY,
  NAVIGATION_COPY,
  FORM_COPY,
  MODERATION_COPY,
  LEGAL_COPY
};

export default COPY;
