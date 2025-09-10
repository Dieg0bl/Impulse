// Media Upload Service for IMPULSE LEAN v1 - Phase 8
// Handles file uploads with security, validation, and S3/MinIO integration

import React from 'react'
import { apiClient } from './api'

// =============================================================================
// TYPES & INTERFACES
// =============================================================================

export interface MediaFile {
  uuid: string
  originalName: string
  size: number
  mimeType: string
  url: string
  thumbnailUrl?: string
  uploadedAt: string
  userId: string
  challengeId?: string
  evidenceId?: string
  status: 'PENDING' | 'PROCESSING' | 'READY' | 'FAILED'
  metadata?: {
    width?: number
    height?: number
    duration?: number
    exifStripped: boolean
    antivirusScanned: boolean
    antivirusResult?: 'CLEAN' | 'INFECTED' | 'SUSPICIOUS'
  }
}

export interface UploadProgressCallback {
  (progress: number): void
}

export interface UploadRequest {
  file: File
  challengeId?: string
  evidenceId?: string
  isPrivate?: boolean
  onProgress?: UploadProgressCallback
}

export interface PresignedUploadResponse {
  uploadUrl: string
  mediaId: string
  fields?: Record<string, string>
}

export interface MediaUploadResponse {
  media: MediaFile
  uploadUrl?: string
}

// =============================================================================
// FILE VALIDATION
// =============================================================================

const ALLOWED_IMAGE_TYPES = [
  'image/jpeg',
  'image/jpg', 
  'image/png',
  'image/gif',
  'image/webp'
]

const ALLOWED_VIDEO_TYPES = [
  'video/mp4',
  'video/webm',
  'video/ogg',
  'video/avi',
  'video/mov',
  'video/quicktime'
]

const ALLOWED_DOCUMENT_TYPES = [
  'application/pdf',
  'text/plain',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
]

const MAX_FILE_SIZE = {
  IMAGE: 10 * 1024 * 1024, // 10MB
  VIDEO: 100 * 1024 * 1024, // 100MB
  DOCUMENT: 5 * 1024 * 1024 // 5MB
}

export class FileValidationError extends Error {
  constructor(message: string, public code: string) {
    super(message)
    this.name = 'FileValidationError'
  }
}

function validateFile(file: File): void {
  // Check file size
  const fileType = getFileCategory(file.type)
  const maxSize = MAX_FILE_SIZE[fileType]
  
  if (file.size > maxSize) {
    throw new FileValidationError(
      `File size exceeds maximum allowed size of ${formatFileSize(maxSize)}`,
      'FILE_TOO_LARGE'
    )
  }

  // Check file type
  const isAllowedType = [
    ...ALLOWED_IMAGE_TYPES,
    ...ALLOWED_VIDEO_TYPES,
    ...ALLOWED_DOCUMENT_TYPES
  ].includes(file.type)

  if (!isAllowedType) {
    throw new FileValidationError(
      `File type ${file.type} is not allowed`,
      'INVALID_FILE_TYPE'
    )
  }

  // Check file name
  if (file.name.length > 255) {
    throw new FileValidationError(
      'File name is too long (maximum 255 characters)',
      'FILENAME_TOO_LONG'
    )
  }

  // Basic security checks
  const suspiciousExtensions = ['.exe', '.bat', '.cmd', '.com', '.scr', '.pif']
  const hasExtension = suspiciousExtensions.some(ext => 
    file.name.toLowerCase().endsWith(ext)
  )
  
  if (hasExtension) {
    throw new FileValidationError(
      'File type not allowed for security reasons',
      'SECURITY_VIOLATION'
    )
  }
}

function getFileCategory(mimeType: string): 'IMAGE' | 'VIDEO' | 'DOCUMENT' {
  if (ALLOWED_IMAGE_TYPES.includes(mimeType)) return 'IMAGE'
  if (ALLOWED_VIDEO_TYPES.includes(mimeType)) return 'VIDEO'
  return 'DOCUMENT'
}

function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// =============================================================================
// MEDIA UPLOAD SERVICE
// =============================================================================

export class MediaUploadService {
  private api: typeof apiClient

  constructor(api: typeof apiClient) {
    this.api = api
  }

  /**
   * Upload a single file with progress tracking
   */
  async uploadFile(request: UploadRequest): Promise<MediaFile> {
    const { file, challengeId, evidenceId, isPrivate = false, onProgress } = request

    try {
      // Validate file first
      validateFile(file)

      // Step 1: Request presigned upload URL
      const presignedResponse = await this.requestPresignedUpload({
        fileName: file.name,
        fileSize: file.size,
        mimeType: file.type,
        challengeId,
        evidenceId,
        isPrivate
      })

      // Step 2: Upload to S3/MinIO using presigned URL
      const uploadedFile = await this.uploadToStorage(
        file,
        presignedResponse,
        onProgress
      )

      // Step 3: Confirm upload completion
      const mediaFile = await this.confirmUpload(presignedResponse.mediaId)

      return mediaFile
    } catch (error) {
      console.error('File upload failed:', error)
      throw error
    }
  }

  /**
   * Upload multiple files with batch progress tracking
   */
  async uploadMultipleFiles(
    files: File[],
    options: {
      challengeId?: string
      evidenceId?: string
      isPrivate?: boolean
      onProgress?: (fileIndex: number, progress: number) => void
      onFileComplete?: (fileIndex: number, media: MediaFile) => void
    } = {}
  ): Promise<MediaFile[]> {
    const results: MediaFile[] = []
    const errors: Array<{ file: File; error: Error }> = []

    for (let i = 0; i < files.length; i++) {
      const file = files[i]
      
      try {
        const media = await this.uploadFile({
          file,
          challengeId: options.challengeId,
          evidenceId: options.evidenceId,
          isPrivate: options.isPrivate,
          onProgress: (progress) => options.onProgress?.(i, progress)
        })
        
        results.push(media)
        options.onFileComplete?.(i, media)
      } catch (error) {
        console.error(`Failed to upload file ${file.name}:`, error)
        errors.push({ file, error: error as Error })
      }
    }

    if (errors.length > 0) {
      console.warn(`${errors.length} files failed to upload:`, errors)
    }

    return results
  }

  /**
   * Request presigned upload URL from backend
   */
  private async requestPresignedUpload(params: {
    fileName: string
    fileSize: number
    mimeType: string
    challengeId?: string
    evidenceId?: string
    isPrivate: boolean
  }): Promise<PresignedUploadResponse> {
    return this.api.post<PresignedUploadResponse>('/media/presigned-upload', params)
  }

  /**
   * Upload file to S3/MinIO using presigned URL
   */
  private async uploadToStorage(
    file: File,
    presignedResponse: PresignedUploadResponse,
    onProgress?: UploadProgressCallback
  ): Promise<void> {
    const formData = new FormData()
    
    // Add any required fields from presigned response
    if (presignedResponse.fields) {
      Object.entries(presignedResponse.fields).forEach(([key, value]) => {
        formData.append(key, value)
      })
    }
    
    // Add the file last (required by S3)
    formData.append('file', file)

    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest()

      // Track upload progress
      if (onProgress) {
        xhr.upload.addEventListener('progress', (event) => {
          if (event.lengthComputable) {
            const progress = (event.loaded / event.total) * 100
            onProgress(Math.round(progress))
          }
        })
      }

      xhr.addEventListener('load', () => {
        if (xhr.status >= 200 && xhr.status < 300) {
          resolve()
        } else {
          reject(new Error(`Upload failed with status ${xhr.status}: ${xhr.statusText}`))
        }
      })

      xhr.addEventListener('error', () => {
        reject(new Error('Upload failed due to network error'))
      })

      xhr.addEventListener('abort', () => {
        reject(new Error('Upload was aborted'))
      })

      xhr.open('POST', presignedResponse.uploadUrl)
      xhr.send(formData)
    })
  }

  /**
   * Confirm upload completion with backend
   */
  private async confirmUpload(mediaId: string): Promise<MediaFile> {
    const response = await this.api.post<{ media: MediaFile }>(`/media/${mediaId}/confirm-upload`, {})
    return response.media
  }

  /**
   * Get media file by ID
   */
  async getMedia(mediaId: string): Promise<MediaFile> {
    const response = await this.api.get<{ media: MediaFile }>(`/media/${mediaId}`)
    return response.media
  }

  /**
   * Delete media file
   */
  async deleteMedia(mediaId: string): Promise<void> {
    await this.api.delete(`/media/${mediaId}`)
  }

  /**
   * Get media files for a challenge
   */
  async getChallengeMedia(challengeId: string): Promise<MediaFile[]> {
    const response = await this.api.get<{ media: MediaFile[] }>(`/challenges/${challengeId}/media`)
    return response.media
  }

  /**
   * Get media files for evidence
   */
  async getEvidenceMedia(evidenceId: string): Promise<MediaFile[]> {
    const response = await this.api.get<{ media: MediaFile[] }>(`/evidence/${evidenceId}/media`)
    return response.media
  }

  /**
   * Generate download URL for private media
   */
  async getDownloadUrl(mediaId: string): Promise<string> {
    const response = await this.api.post<{ downloadUrl: string }>(`/media/${mediaId}/download-url`, {})
    return response.downloadUrl
  }
}

// =============================================================================
// REACT HOOKS
// =============================================================================

export interface UseMediaUploadResult {
  uploadFile: (request: UploadRequest) => Promise<MediaFile>
  uploadMultipleFiles: (
    files: File[],
    options?: {
      challengeId?: string
      evidenceId?: string
      isPrivate?: boolean
      onProgress?: (fileIndex: number, progress: number) => void
      onFileComplete?: (fileIndex: number, media: MediaFile) => void
    }
  ) => Promise<MediaFile[]>
  isUploading: boolean
  progress: number
  error: string | null
}

export function useMediaUpload(): UseMediaUploadResult {
  const [isUploading, setIsUploading] = React.useState(false)
  const [progress, setProgress] = React.useState(0)
  const [error, setError] = React.useState<string | null>(null)

  const mediaService = React.useMemo(
    () => new MediaUploadService(apiClient),
    []
  )

  const uploadFile = React.useCallback(
    async (request: UploadRequest): Promise<MediaFile> => {
      setIsUploading(true)
      setError(null)
      setProgress(0)

      try {
        const media = await mediaService.uploadFile({
          ...request,
          onProgress: (progress) => {
            setProgress(progress)
            request.onProgress?.(progress)
          }
        })

        setProgress(100)
        return media
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Upload failed'
        setError(errorMessage)
        throw err
      } finally {
        setIsUploading(false)
      }
    },
    [mediaService]
  )

  const uploadMultipleFiles = React.useCallback(
    async (files: File[], options = {}): Promise<MediaFile[]> => {
      setIsUploading(true)
      setError(null)
      setProgress(0)

      try {
        const results = await mediaService.uploadMultipleFiles(files, {
          ...options,
          onProgress: (fileIndex, fileProgress) => {
            const totalProgress = ((fileIndex * 100) + fileProgress) / files.length
            setProgress(totalProgress)
            options.onProgress?.(fileIndex, fileProgress)
          }
        })

        setProgress(100)
        return results
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Upload failed'
        setError(errorMessage)
        throw err
      } finally {
        setIsUploading(false)
      }
    },
    [mediaService]
  )

  return {
    uploadFile,
    uploadMultipleFiles,
    isUploading,
    progress,
    error
  }
}

// Export singleton instance
let mediaUploadServiceInstance: MediaUploadService | null = null

export function getMediaUploadService(): MediaUploadService {
  if (!mediaUploadServiceInstance) {
    mediaUploadServiceInstance = new MediaUploadService(apiClient)
  }
  return mediaUploadServiceInstance
}
