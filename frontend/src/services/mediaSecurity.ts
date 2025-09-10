// Media Security & Processing Service for IMPULSE LEAN v1 - Phase 8
// Handles antivirus scanning, EXIF stripping, and secure storage

import { apiClient } from './api'

// =============================================================================
// SECURITY TYPES
// =============================================================================

export interface AntivirusResult {
  status: 'CLEAN' | 'INFECTED' | 'SUSPICIOUS' | 'SCANNING' | 'ERROR'
  scanDate: string
  engine: string
  version: string
  threats?: string[]
  details?: string
}

export interface ExifData {
  camera?: string
  location?: {
    latitude: number
    longitude: number
  }
  timestamp?: string
  [key: string]: any
}

export interface MediaProcessingResult {
  mediaId: string
  originalSize: number
  processedSize: number
  antivirus: AntivirusResult
  exifStripped: boolean
  originalExif?: ExifData
  thumbnailGenerated: boolean
  processingTime: number
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
  errors?: string[]
}

// =============================================================================
// STORAGE CONFIGURATION
// =============================================================================

export interface StorageConfig {
  provider: 'S3' | 'MINIO' | 'LOCAL'
  bucket: string
  region?: string
  publicUrl?: string
  cdnUrl?: string
  encryption: boolean
  versioning: boolean
}

export interface PresignedUrlRequest {
  fileName: string
  contentType: string
  size: number
  expiresIn?: number
  operation: 'upload' | 'download'
  isPrivate?: boolean
}

export interface PresignedUrlResponse {
  url: string
  fields?: Record<string, string>
  expiresAt: string
  uploadId?: string
}

// =============================================================================
// MEDIA SECURITY SERVICE
// =============================================================================

export class MediaSecurityService {
  private api: typeof apiClient

  constructor(api: typeof apiClient) {
    this.api = api
  }

  /**
   * Scan file for viruses and malware
   */
  async scanFile(mediaId: string): Promise<AntivirusResult> {
    const response = await this.api.post<{ result: AntivirusResult }>(
      `/media/${mediaId}/antivirus-scan`, 
      {}
    )
    return response.result
  }

  /**
   * Get antivirus scan status
   */
  async getScanStatus(mediaId: string): Promise<AntivirusResult> {
    const response = await this.api.get<{ result: AntivirusResult }>(
      `/media/${mediaId}/antivirus-status`
    )
    return response.result
  }

  /**
   * Strip EXIF data from image
   */
  async stripExifData(mediaId: string): Promise<{ success: boolean; originalExif?: ExifData }> {
    const response = await this.api.post<{ 
      success: boolean
      originalExif?: ExifData 
    }>(`/media/${mediaId}/strip-exif`, {})
    
    return response
  }

  /**
   * Get media processing status
   */
  async getProcessingStatus(mediaId: string): Promise<MediaProcessingResult> {
    const response = await this.api.get<{ result: MediaProcessingResult }>(
      `/media/${mediaId}/processing-status`
    )
    return response.result
  }

  /**
   * Generate thumbnail for image/video
   */
  async generateThumbnail(
    mediaId: string, 
    options?: { 
      width?: number
      height?: number
      quality?: number
      format?: 'jpg' | 'png' | 'webp'
    }
  ): Promise<{ thumbnailUrl: string }> {
    const response = await this.api.post<{ thumbnailUrl: string }>(
      `/media/${mediaId}/generate-thumbnail`, 
      options || {}
    )
    return response
  }

  /**
   * Quarantine suspicious file
   */
  async quarantineFile(mediaId: string, reason: string): Promise<void> {
    await this.api.post(`/media/${mediaId}/quarantine`, { reason })
  }

  /**
   * Release file from quarantine
   */
  async releaseFromQuarantine(mediaId: string): Promise<void> {
    await this.api.post(`/media/${mediaId}/release-quarantine`, {})
  }
}

// =============================================================================
// STORAGE SERVICE
// =============================================================================

export class StorageService {
  private api: typeof apiClient

  constructor(api: typeof apiClient) {
    this.api = api
  }

  /**
   * Get storage configuration
   */
  async getStorageConfig(): Promise<StorageConfig> {
    const response = await this.api.get<{ config: StorageConfig }>('/storage/config')
    return response.config
  }

  /**
   * Request presigned URL for upload/download
   */
  async getPresignedUrl(request: PresignedUrlRequest): Promise<PresignedUrlResponse> {
    const response = await this.api.post<PresignedUrlResponse>('/storage/presigned-url', request)
    return response
  }

  /**
   * Copy file to different location
   */
  async copyFile(
    sourceKey: string, 
    destinationKey: string, 
    options?: {
      sourceVersion?: string
      destinationBucket?: string
      metadata?: Record<string, string>
    }
  ): Promise<{ success: boolean; destinationUrl: string }> {
    const response = await this.api.post<{ 
      success: boolean
      destinationUrl: string 
    }>('/storage/copy', {
      sourceKey,
      destinationKey,
      ...options
    })
    return response
  }

  /**
   * Move file to different location
   */
  async moveFile(
    sourceKey: string, 
    destinationKey: string,
    options?: {
      destinationBucket?: string
      metadata?: Record<string, string>
    }
  ): Promise<{ success: boolean; destinationUrl: string }> {
    const response = await this.api.post<{ 
      success: boolean
      destinationUrl: string 
    }>('/storage/move', {
      sourceKey,
      destinationKey,
      ...options
    })
    return response
  }

  /**
   * Delete file from storage
   */
  async deleteFile(key: string, version?: string): Promise<{ success: boolean }> {
    const response = await this.api.delete<{ success: boolean }>(
      `/storage/delete/${encodeURIComponent(key)}${version ? `?version=${version}` : ''}`
    )
    return response
  }

  /**
   * Get file metadata
   */
  async getFileMetadata(key: string): Promise<{
    size: number
    lastModified: string
    contentType: string
    etag: string
    version?: string
    metadata?: Record<string, string>
  }> {
    const response = await this.api.get<{
      size: number
      lastModified: string
      contentType: string
      etag: string
      version?: string
      metadata?: Record<string, string>
    }>(`/storage/metadata/${encodeURIComponent(key)}`)
    return response
  }

  /**
   * List files in bucket/folder
   */
  async listFiles(
    prefix?: string,
    options?: {
      maxKeys?: number
      marker?: string
      delimiter?: string
    }
  ): Promise<{
    files: Array<{
      key: string
      size: number
      lastModified: string
      etag: string
    }>
    folders: string[]
    isTruncated: boolean
    nextMarker?: string
  }> {
    const queryParams = new URLSearchParams()
    if (prefix) queryParams.append('prefix', prefix)
    if (options?.maxKeys) queryParams.append('maxKeys', options.maxKeys.toString())
    if (options?.marker) queryParams.append('marker', options.marker)
    if (options?.delimiter) queryParams.append('delimiter', options.delimiter)

    const response = await this.api.get<{
      files: Array<{
        key: string
        size: number
        lastModified: string
        etag: string
      }>
      folders: string[]
      isTruncated: boolean
      nextMarker?: string
    }>(`/storage/list?${queryParams.toString()}`)
    
    return response
  }
}

// =============================================================================
// MEDIA PIPELINE SERVICE
// =============================================================================

export class MediaPipelineService {
  private securityService: MediaSecurityService
  private storageService: StorageService

  constructor(api: typeof apiClient) {
    this.securityService = new MediaSecurityService(api)
    this.storageService = new StorageService(api)
  }

  /**
   * Process uploaded media through security pipeline
   */
  async processMedia(mediaId: string): Promise<MediaProcessingResult> {
    try {
      // Step 1: Antivirus scan
      const antivirusResult = await this.securityService.scanFile(mediaId)
      
      if (antivirusResult.status === 'INFECTED' || antivirusResult.status === 'SUSPICIOUS') {
        await this.securityService.quarantineFile(
          mediaId, 
          `Antivirus detection: ${antivirusResult.threats?.join(', ') || 'Unknown threat'}`
        )
        throw new Error(`File quarantined due to security threat: ${antivirusResult.status}`)
      }

      // Step 2: Strip EXIF data for images
      const exifResult = await this.securityService.stripExifData(mediaId)

      // Step 3: Generate thumbnail
      try {
        await this.securityService.generateThumbnail(mediaId, {
          width: 300,
          height: 300,
          quality: 80,
          format: 'jpg'
        })
      } catch (error) {
        console.warn('Thumbnail generation failed:', error)
        // Non-critical error, continue processing
      }

      // Get final processing status
      const processingResult = await this.securityService.getProcessingStatus(mediaId)
      return processingResult

    } catch (error) {
      console.error('Media processing failed:', error)
      throw error
    }
  }

  /**
   * Batch process multiple media files
   */
  async batchProcessMedia(mediaIds: string[]): Promise<MediaProcessingResult[]> {
    const results: MediaProcessingResult[] = []
    const errors: Array<{ mediaId: string; error: Error }> = []

    for (const mediaId of mediaIds) {
      try {
        const result = await this.processMedia(mediaId)
        results.push(result)
      } catch (error) {
        console.error(`Failed to process media ${mediaId}:`, error)
        errors.push({ mediaId, error: error as Error })
      }
    }

    if (errors.length > 0) {
      console.warn(`${errors.length} media files failed processing:`, errors)
    }

    return results
  }

  /**
   * Clean up temporary files and failed uploads
   */
  async cleanupMedia(olderThanHours: number = 24): Promise<{ cleaned: number }> {
    const response = await this.storageService.api.post<{ cleaned: number }>(
      '/media/cleanup',
      { olderThanHours }
    )
    return response
  }
}

// =============================================================================
// REACT HOOKS
// =============================================================================

export function useMediaSecurity() {
  const securityService = React.useMemo(
    () => new MediaSecurityService(apiClient),
    []
  )

  const scanFile = React.useCallback(
    async (mediaId: string) => {
      return securityService.scanFile(mediaId)
    },
    [securityService]
  )

  const getScanStatus = React.useCallback(
    async (mediaId: string) => {
      return securityService.getScanStatus(mediaId)
    },
    [securityService]
  )

  const stripExif = React.useCallback(
    async (mediaId: string) => {
      return securityService.stripExifData(mediaId)
    },
    [securityService]
  )

  return {
    scanFile,
    getScanStatus,
    stripExif,
    securityService
  }
}

export function useMediaPipeline() {
  const pipelineService = React.useMemo(
    () => new MediaPipelineService(apiClient),
    []
  )

  const processMedia = React.useCallback(
    async (mediaId: string) => {
      return pipelineService.processMedia(mediaId)
    },
    [pipelineService]
  )

  const batchProcess = React.useCallback(
    async (mediaIds: string[]) => {
      return pipelineService.batchProcessMedia(mediaIds)
    },
    [pipelineService]
  )

  return {
    processMedia,
    batchProcess,
    pipelineService
  }
}

// Export singleton instances
export const mediaSecurityService = new MediaSecurityService(apiClient)
export const storageService = new StorageService(apiClient)
export const mediaPipelineService = new MediaPipelineService(apiClient)
