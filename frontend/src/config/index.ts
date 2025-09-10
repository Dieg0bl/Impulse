// Application Configuration for IMPULSE LEAN v1
// Centralized config management for Phases 7 & 8

// =============================================================================
// ENVIRONMENT CONFIGURATION
// =============================================================================

interface AppConfig {
  // API Configuration
  api: {
    baseUrl: string
    timeout: number
    retryAttempts: number
  }
  
  // Authentication
  auth: {
    jwtExpiration: number
    refreshThreshold: number
  }
  
  // Feature Flags
  features: {
    billingEnabled: boolean
    mediaUploadEnabled: boolean
    antivirusEnabled: boolean
    exifStrippingEnabled: boolean
    thumbnailGenerationEnabled: boolean
  }
  
  // Media Storage
  media: {
    provider: 'S3' | 'MINIO' | 'LOCAL'
    bucket: string
    region: string
    publicUrl: string
    cdnUrl?: string
    limits: {
      maxFileSizeMB: number
      maxFilesPerUpload: number
      allowedImageTypes: string[]
      allowedVideoTypes: string[]
      allowedDocumentTypes: string[]
    }
  }
  
  // Security
  security: {
    antivirusEngine: string
    scanTimeout: number
    quarantineOnSuspicious: boolean
    exif: {
      stripLocationData: boolean
      stripCameraData: boolean
      stripAllMetadata: boolean
    }
  }
  
  // Thumbnails
  thumbnails: {
    width: number
    height: number
    quality: number
    format: 'jpg' | 'png' | 'webp'
  }
  
  // Presigned URLs
  presignedUrls: {
    defaultExpiration: number
    uploadExpiration: number
    downloadExpiration: number
  }
  
  // CDN & Caching
  cdn: {
    enabled: boolean
    cacheTtl: number
    browserCacheTtl: number
  }
  
  // Monitoring
  monitoring: {
    logLevel: 'debug' | 'info' | 'warn' | 'error'
    analyticsEnabled: boolean
    sentryDsn?: string
    gaTrackingId?: string
    anonymizeIp: boolean
  }
  
  // Development
  development: {
    mockUploads: boolean
    bypassAntivirus: boolean
    skipExifStripping: boolean
    debugMediaPipeline: boolean
  }
}

// =============================================================================
// CONFIG LOADING
// =============================================================================

function loadConfig(): AppConfig {
  return {
    api: {
      baseUrl: import.meta.env.VITE_API_BASE || 'http://localhost:8080',
      timeout: Number(import.meta.env.VITE_API_TIMEOUT) || 30000,
      retryAttempts: Number(import.meta.env.VITE_API_RETRY_ATTEMPTS) || 3
    },
    
    auth: {
      jwtExpiration: Number(import.meta.env.VITE_JWT_EXPIRATION) || 86400,
      refreshThreshold: Number(import.meta.env.VITE_REFRESH_TOKEN_THRESHOLD) || 300
    },
    
    features: {
      billingEnabled: import.meta.env.VITE_BILLING_ON === 'true',
      mediaUploadEnabled: import.meta.env.VITE_MEDIA_UPLOAD_ON !== 'false',
      antivirusEnabled: import.meta.env.VITE_ANTIVIRUS_SCAN_ON !== 'false',
      exifStrippingEnabled: import.meta.env.VITE_EXIF_STRIPPING_ON !== 'false',
      thumbnailGenerationEnabled: import.meta.env.VITE_THUMBNAIL_GENERATION_ON !== 'false'
    },
    
    media: {
      provider: (import.meta.env.VITE_STORAGE_PROVIDER as 'S3' | 'MINIO' | 'LOCAL') || 'MINIO',
      bucket: import.meta.env.VITE_STORAGE_BUCKET || 'impulse-media',
      region: import.meta.env.VITE_STORAGE_REGION || 'us-east-1',
      publicUrl: import.meta.env.VITE_STORAGE_PUBLIC_URL || 'http://localhost:9000',
      cdnUrl: import.meta.env.VITE_STORAGE_CDN_URL,
      limits: {
        maxFileSizeMB: Number(import.meta.env.VITE_MAX_FILE_SIZE_MB) || 100,
        maxFilesPerUpload: Number(import.meta.env.VITE_MAX_FILES_PER_UPLOAD) || 10,
        allowedImageTypes: (import.meta.env.VITE_ALLOWED_IMAGE_TYPES || 'image/jpeg,image/png,image/gif,image/webp').split(','),
        allowedVideoTypes: (import.meta.env.VITE_ALLOWED_VIDEO_TYPES || 'video/mp4,video/webm,video/ogg').split(','),
        allowedDocumentTypes: (import.meta.env.VITE_ALLOWED_DOCUMENT_TYPES || 'application/pdf,text/plain').split(',')
      }
    },
    
    security: {
      antivirusEngine: import.meta.env.VITE_ANTIVIRUS_ENGINE || 'CLAMAV',
      scanTimeout: Number(import.meta.env.VITE_SCAN_TIMEOUT) || 60000,
      quarantineOnSuspicious: import.meta.env.VITE_QUARANTINE_ON_SUSPICIOUS !== 'false',
      exif: {
        stripLocationData: import.meta.env.VITE_STRIP_LOCATION_DATA !== 'false',
        stripCameraData: import.meta.env.VITE_STRIP_CAMERA_DATA !== 'false',
        stripAllMetadata: import.meta.env.VITE_STRIP_ALL_METADATA === 'true'
      }
    },
    
    thumbnails: {
      width: Number(import.meta.env.VITE_THUMBNAIL_WIDTH) || 300,
      height: Number(import.meta.env.VITE_THUMBNAIL_HEIGHT) || 300,
      quality: Number(import.meta.env.VITE_THUMBNAIL_QUALITY) || 80,
      format: (import.meta.env.VITE_THUMBNAIL_FORMAT as 'jpg' | 'png' | 'webp') || 'jpg'
    },
    
    presignedUrls: {
      defaultExpiration: Number(import.meta.env.VITE_PRESIGNED_URL_EXPIRATION) || 3600,
      uploadExpiration: Number(import.meta.env.VITE_UPLOAD_URL_EXPIRATION) || 1800,
      downloadExpiration: Number(import.meta.env.VITE_DOWNLOAD_URL_EXPIRATION) || 300
    },
    
    cdn: {
      enabled: import.meta.env.VITE_ENABLE_CDN === 'true',
      cacheTtl: Number(import.meta.env.VITE_CDN_CACHE_TTL) || 86400,
      browserCacheTtl: Number(import.meta.env.VITE_BROWSER_CACHE_TTL) || 3600
    },
    
    monitoring: {
      logLevel: (import.meta.env.VITE_LOG_LEVEL as 'debug' | 'info' | 'warn' | 'error') || 'info',
      analyticsEnabled: import.meta.env.VITE_ENABLE_ANALYTICS !== 'false',
      sentryDsn: import.meta.env.VITE_SENTRY_DSN,
      gaTrackingId: import.meta.env.VITE_GA_TRACKING_ID,
      anonymizeIp: import.meta.env.VITE_GA_ANONYMIZE_IP !== 'false'
    },
    
    development: {
      mockUploads: import.meta.env.VITE_MOCK_UPLOADS === 'true',
      bypassAntivirus: import.meta.env.VITE_BYPASS_ANTIVIRUS === 'true',
      skipExifStripping: import.meta.env.VITE_SKIP_EXIF_STRIPPING === 'true',
      debugMediaPipeline: import.meta.env.VITE_DEBUG_MEDIA_PIPELINE === 'true'
    }
  }
}

// =============================================================================
// CONFIG VALIDATION
// =============================================================================

function validateConfig(config: AppConfig): void {
  const errors: string[] = []
  
  // Validate API configuration
  if (!config.api.baseUrl) {
    errors.push('API base URL is required')
  }
  
  if (config.api.timeout < 1000) {
    errors.push('API timeout must be at least 1000ms')
  }
  
  // Validate media configuration
  if (!config.media.bucket) {
    errors.push('Storage bucket name is required')
  }
  
  if (config.media.limits.maxFileSizeMB < 1) {
    errors.push('Max file size must be at least 1MB')
  }
  
  if (config.media.limits.maxFilesPerUpload < 1) {
    errors.push('Max files per upload must be at least 1')
  }
  
  // Validate thumbnail configuration
  if (config.thumbnails.width < 50 || config.thumbnails.height < 50) {
    errors.push('Thumbnail dimensions must be at least 50x50')
  }
  
  if (config.thumbnails.quality < 1 || config.thumbnails.quality > 100) {
    errors.push('Thumbnail quality must be between 1 and 100')
  }
  
  // Validate presigned URL configuration
  if (config.presignedUrls.uploadExpiration < 60) {
    errors.push('Upload URL expiration must be at least 60 seconds')
  }
  
  if (errors.length > 0) {
    console.error('Configuration validation errors:', errors)
    throw new Error(`Invalid configuration: ${errors.join(', ')}`)
  }
}

// =============================================================================
// EXPORTS
// =============================================================================

const config = loadConfig()

// Validate configuration in development
if (import.meta.env.MODE === 'development') {
  try {
    validateConfig(config)
    console.log('✅ Configuration loaded successfully')
  } catch (error) {
    console.error('❌ Configuration validation failed:', error)
    throw error
  }
}

export default config
export type { AppConfig }

// =============================================================================
// CONFIGURATION HELPERS
// =============================================================================

export const isDevelopment = import.meta.env.MODE === 'development'
export const isProduction = import.meta.env.MODE === 'production'
export const isBillingEnabled = config.features.billingEnabled
export const isMediaUploadEnabled = config.features.mediaUploadEnabled

// Media type checking helpers
export function isImageType(mimeType: string): boolean {
  return config.media.limits.allowedImageTypes.includes(mimeType)
}

export function isVideoType(mimeType: string): boolean {
  return config.media.limits.allowedVideoTypes.includes(mimeType)
}

export function isDocumentType(mimeType: string): boolean {
  return config.media.limits.allowedDocumentTypes.includes(mimeType)
}

export function isAllowedFileType(mimeType: string): boolean {
  return isImageType(mimeType) || isVideoType(mimeType) || isDocumentType(mimeType)
}

// File size validation
export function isFileSizeValid(sizeInBytes: number): boolean {
  const maxSizeInBytes = config.media.limits.maxFileSizeMB * 1024 * 1024
  return sizeInBytes <= maxSizeInBytes
}

// URL builders
export function buildMediaUrl(key: string): string {
  const baseUrl = config.media.cdnUrl || config.media.publicUrl
  return `${baseUrl}/${config.media.bucket}/${key}`
}

export function buildThumbnailUrl(key: string): string {
  const baseUrl = config.media.cdnUrl || config.media.publicUrl
  return `${baseUrl}/${config.media.bucket}/thumbnails/${key}`
}

// Logging helper
export function log(level: 'debug' | 'info' | 'warn' | 'error', message: string, ...args: any[]): void {
  const levels = { debug: 0, info: 1, warn: 2, error: 3 }
  const configLevel = levels[config.monitoring.logLevel]
  const messageLevel = levels[level]
  
  if (messageLevel >= configLevel) {
    console[level](`[IMPULSE-${level.toUpperCase()}]`, message, ...args)
  }
}
