// Configuration service for feature toggles
// This manages the "interruptores" from the specification

interface AppConfig {
  // Phase 3 & 4: Billing and fiscal toggles (Section 18)
  BILLING_ON: boolean; // Controls if billing is active
  COACH_MARKET_ON: boolean; // Controls Coach marketplace
  ADS_ON: boolean; // Controls ads (Pro always ad-free)
  CMP_ON: boolean; // Controls Cookie Management Platform

  // Beta configuration (Section 2)
  BETA_DAYS_REMAINING: number; // 90 days from specification
  BETA_END_DATE: string;
  BETA_ALERTS_ENABLED: boolean; // D-15, D-7, D-1 alerts

  // Legal and contact info (Section 1 - specification compliant)
  COMPANY_NAME: string; // IMPULSE
  OWNER_NAME: string; // Diego Barreiro Liste
  SUPPORT_EMAIL: string; // impulse.soporte@gmail.com
  LEGAL_EMAIL: string; // impulse.legal@gmail.com
  ABUSE_EMAIL: string; // impulse.abuse@gmail.com
  ADDRESS: string; // Oroso (A Coruña), España, 15688

  // Technical stack configuration (Section 13)
  DATABASE_TYPE: "mysql" | "postgresql";
  DATABASE_VERSION: string;
  CHARSET: string; // utf8mb4
  TIMEZONE: string; // UTC

  // Security configuration (Section 8)
  JWT_ALGORITHM: string; // RS256
  ACCESS_TOKEN_DURATION: number; // 15 minutes
  REFRESH_TOKEN_DURATION: number; // 14 days
  MIN_AGE: number; // 18 years

  // Privacy defaults (Section 0)
  DEFAULT_VISIBILITY: "private" | "validators" | "public"; // private by default
  GDPR_FIRST: boolean;
  DATA_MINIMIZATION: boolean;

  // Pricing configuration (Section 3 - specification exact)
  CURRENCY: string; // EUR
  BASIC_PRICE: number; // 0 (gratis)
  PRO_PRICE_MONTHLY: number; // 12.99
  PRO_PRICE_YEARLY: number; // 99
  TEAMS_PRICE: number; // 39.99
  TEAMS_INCLUDED_MEMBERS: number; // 10
  TEAMS_EXTRA_MEMBER_PRICE: number; // 4

  // Regional configuration (Section 4)
  TARGET_REGIONS: string[]; // EU first
  STRIPE_TAX_ON: boolean;
  OSS_COMPLIANCE: boolean; // EU VAT/OSS

  // Growth configuration (Section 10)
  REFERRAL_ENABLED: boolean;
  REFERRAL_REWARD: string; // "2x1 mes Pro"
  REFERRAL_LIMIT_MONTHLY: number; // 3/month (ampliable a 5)
  DEEP_LINKS_ENABLED: boolean; // PWA deep links

  // Moderation settings (Section 7)
  MODERATION_SLA_HOURS: number; // 72h
  STRIKE_LIMIT: number; // 3 strikes
  AUTO_MODERATION: boolean;

  // Data retention (Section 9)
  USER_DATA_RETENTION_MONTHS: number; // 12 months
  UGC_RETENTION_MONTHS: number; // 6 months
  LOG_RETENTION_MONTHS: number; // 6 months
  BACKUP_RETENTION_DAYS: number; // 90 days
  INVOICE_RETENTION_YEARS: number; // 6 years
}

// Default configuration for launch phase (Section 2 & 3 - Specification Compliant)
const DEFAULT_CONFIG: AppConfig = {
  // All billing disabled during beta (Section 18)
  BILLING_ON: false,
  COACH_MARKET_ON: false,
  ADS_ON: false,
  CMP_ON: false,

  // Beta configuration (90 days from specification - Section 2)
  BETA_DAYS_REMAINING: 90,
  BETA_END_DATE: "2025-12-10", // Example date
  BETA_ALERTS_ENABLED: true,

  // Legal info (Section 1 specification)
  COMPANY_NAME: "IMPULSE",
  OWNER_NAME: "Diego Barreiro Liste",
  SUPPORT_EMAIL: "impulse.soporte@gmail.com",
  LEGAL_EMAIL: "impulse.legal@gmail.com",
  ABUSE_EMAIL: "impulse.abuse@gmail.com",
  ADDRESS: "Oroso (A Coruña), España, 15688",

  // Technical stack (Section 13 - moving to MySQL 8)
  DATABASE_TYPE: "mysql",
  DATABASE_VERSION: "8.0",
  CHARSET: "utf8mb4",
  TIMEZONE: "UTC",

  // Security (Section 8 - JWT RS256)
  JWT_ALGORITHM: "RS256",
  ACCESS_TOKEN_DURATION: 15, // minutes
  REFRESH_TOKEN_DURATION: 14, // days
  MIN_AGE: 18,

  // Privacy (Section 0 - private by default)
  DEFAULT_VISIBILITY: "private",
  GDPR_FIRST: true,
  DATA_MINIMIZATION: true,

  // Pricing (Section 3 - exact specification)
  CURRENCY: "EUR",
  BASIC_PRICE: 0,
  PRO_PRICE_MONTHLY: 12.99,
  PRO_PRICE_YEARLY: 99,
  TEAMS_PRICE: 39.99,
  TEAMS_INCLUDED_MEMBERS: 10,
  TEAMS_EXTRA_MEMBER_PRICE: 4,

  // Regional (Section 4 - EU first)
  TARGET_REGIONS: ["EU"],
  STRIPE_TAX_ON: true,
  OSS_COMPLIANCE: true,

  // Growth (Section 10)
  REFERRAL_ENABLED: true,
  REFERRAL_REWARD: "2x1 mes Pro",
  REFERRAL_LIMIT_MONTHLY: 3,
  DEEP_LINKS_ENABLED: true,

  // Moderation (Section 7)
  MODERATION_SLA_HOURS: 72,
  STRIKE_LIMIT: 3,
  AUTO_MODERATION: false,

  // Data retention (Section 9)
  USER_DATA_RETENTION_MONTHS: 12,
  UGC_RETENTION_MONTHS: 6,
  LOG_RETENTION_MONTHS: 6,
  BACKUP_RETENTION_DAYS: 90,
  INVOICE_RETENTION_YEARS: 6,
};

class ConfigService {
  private readonly config: AppConfig;

  constructor() {
    // In production, this would come from environment variables or API
    this.config = { ...DEFAULT_CONFIG };

    // Load from localStorage for persistence during development
    const savedConfig = localStorage.getItem("impulse-config");
    if (savedConfig) {
      try {
        const parsed = JSON.parse(savedConfig);
        this.config = { ...this.config, ...parsed };
      } catch (error) {
        console.warn("Failed to parse saved config, using defaults:", error);
        // Clear corrupted config from localStorage
        localStorage.removeItem("impulse-config");
      }
    }
  }

  // Getters for each toggle
  get isBillingEnabled(): boolean {
    return this.config.BILLING_ON;
  }

  get isCoachMarketEnabled(): boolean {
    return this.config.COACH_MARKET_ON;
  }

  get areAdsEnabled(): boolean {
    return this.config.ADS_ON;
  }

  get isCMPEnabled(): boolean {
    return this.config.CMP_ON;
  }

  get betaDaysRemaining(): number {
    return this.config.BETA_DAYS_REMAINING;
  }

  get betaEndDate(): string {
    return this.config.BETA_END_DATE;
  }

  // Legal info getters
  get companyName(): string {
    return this.config.COMPANY_NAME;
  }

  get ownerName(): string {
    return this.config.OWNER_NAME;
  }

  get supportEmail(): string {
    return this.config.SUPPORT_EMAIL;
  }

  get legalEmail(): string {
    return this.config.LEGAL_EMAIL;
  }

  get abuseEmail(): string {
    return this.config.ABUSE_EMAIL;
  }

  get address(): string {
    return this.config.ADDRESS;
  }

  // Admin functions to toggle features (for development/testing)
  toggleBilling(enabled: boolean): void {
    this.config.BILLING_ON = enabled;
    this.saveConfig();
  }

  toggleCoachMarket(enabled: boolean): void {
    this.config.COACH_MARKET_ON = enabled;
    this.saveConfig();
  }

  toggleAds(enabled: boolean): void {
    this.config.ADS_ON = enabled;
    this.saveConfig();
  }

  toggleCMP(enabled: boolean): void {
    this.config.CMP_ON = enabled;
    this.saveConfig();
  }

  updateBetaDaysRemaining(days: number): void {
    this.config.BETA_DAYS_REMAINING = days;
    this.saveConfig();
  }

  // Get button text based on billing state
  getCheckoutButtonText(planId: string): string {
    if (!this.isBillingEnabled) {
      return "Próximamente";
    }

    switch (planId) {
      case "basic":
        return "Plan Actual";
      case "pro":
        return "Seleccionar Pro";
      case "teams":
        return "Seleccionar Teams";
      default:
        return "Seleccionar Plan";
    }
  }

  // Check if a plan should show payment buttons
  shouldShowPaymentButton(planId: string): boolean {
    if (planId === "basic") return false; // Basic is always free
    return this.isBillingEnabled;
  }

  // Get the appropriate message for disabled billing
  getBillingDisabledMessage(): string {
    return "¡Próximamente! Durante la beta de 90 días, todos los usuarios tienen acceso completo sin cobros.";
  }

  private saveConfig(): void {
    try {
      localStorage.setItem("impulse-config", JSON.stringify(this.config));
    } catch (error) {
      console.warn("Failed to save config to localStorage:", error);
      // In case localStorage is full or unavailable, we continue without saving
    }
  }

  // Get full config (for admin panel)
  getFullConfig(): AppConfig {
    return { ...this.config };
  }
}

// Singleton instance
export const configService = new ConfigService();

// React hook for using config in components
export const useConfig = () => {
  return {
    isBillingEnabled: configService.isBillingEnabled,
    isCoachMarketEnabled: configService.isCoachMarketEnabled,
    areAdsEnabled: configService.areAdsEnabled,
    isCMPEnabled: configService.isCMPEnabled,
    betaDaysRemaining: configService.betaDaysRemaining,
    betaEndDate: configService.betaEndDate,
    companyName: configService.companyName,
    ownerName: configService.ownerName,
    supportEmail: configService.supportEmail,
    legalEmail: configService.legalEmail,
    abuseEmail: configService.abuseEmail,
    address: configService.address,
    getCheckoutButtonText: configService.getCheckoutButtonText.bind(configService),
    shouldShowPaymentButton: configService.shouldShowPaymentButton.bind(configService),
    getBillingDisabledMessage: configService.getBillingDisabledMessage.bind(configService),
  };
};
