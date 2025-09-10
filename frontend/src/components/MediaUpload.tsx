// Media Upload Component for IMPULSE LEAN v1 - Phase 8
// Drag & drop file upload with progress tracking

import React, { useState, useRef, useCallback } from 'react'
import { Upload, X, FileText, Image, Video, AlertCircle, CheckCircle } from 'lucide-react'
import { useMediaUpload, MediaFile, FileValidationError } from '../services/media'

interface MediaUploadProps {
  challengeId?: string
  evidenceId?: string
  isPrivate?: boolean
  multiple?: boolean
  accept?: string[]
  maxFiles?: number
  onUploadComplete?: (files: MediaFile[]) => void
  onUploadError?: (error: string) => void
  className?: string
}

interface UploadingFile {
  file: File
  progress: number
  status: 'uploading' | 'completed' | 'error'
  media?: MediaFile
  error?: string
}

const MediaUpload: React.FC<MediaUploadProps> = ({
  challengeId,
  evidenceId,
  isPrivate = false,
  multiple = true,
  accept = ['image/*', 'video/*', 'application/pdf'],
  maxFiles = 10,
  onUploadComplete,
  onUploadError,
  className = ''
}) => {
  const [uploadingFiles, setUploadingFiles] = useState<UploadingFile[]>([])
  const [isDragOver, setIsDragOver] = useState(false)
  const fileInputRef = useRef<HTMLInputElement>(null)
  
  const { uploadFile, uploadMultipleFiles } = useMediaUpload()

  const handleFileSelect = useCallback(async (files: FileList) => {
    const fileArray = Array.from(files)
    
    // Validate file count
    if (fileArray.length > maxFiles) {
      onUploadError?.(`Maximum ${maxFiles} files allowed`)
      return
    }

    // Initialize uploading files state
    const initialState: UploadingFile[] = fileArray.map(file => ({
      file,
      progress: 0,
      status: 'uploading'
    }))
    
    setUploadingFiles(initialState)

    try {
      if (multiple && fileArray.length > 1) {
        // Upload multiple files
        const results = await uploadMultipleFiles(fileArray, {
          challengeId,
          evidenceId,
          isPrivate,
          onProgress: (fileIndex, progress) => {
            setUploadingFiles(prev => prev.map((item, index) => 
              index === fileIndex 
                ? { ...item, progress }
                : item
            ))
          },
          onFileComplete: (fileIndex, media) => {
            setUploadingFiles(prev => prev.map((item, index) => 
              index === fileIndex 
                ? { ...item, status: 'completed', media, progress: 100 }
                : item
            ))
          }
        })
        
        onUploadComplete?.(results)
      } else {
        // Upload single file
        const file = fileArray[0]
        const media = await uploadFile({
          file,
          challengeId,
          evidenceId,
          isPrivate,
          onProgress: (progress) => {
            setUploadingFiles(prev => prev.map(item => 
              item.file === file 
                ? { ...item, progress }
                : item
            ))
          }
        })

        setUploadingFiles(prev => prev.map(item => 
          item.file === file 
            ? { ...item, status: 'completed', media, progress: 100 }
            : item
        ))

        onUploadComplete?.([media])
      }
    } catch (error) {
      const errorMessage = error instanceof FileValidationError 
        ? error.message 
        : 'Upload failed'
        
      setUploadingFiles(prev => prev.map(item => ({
        ...item,
        status: 'error',
        error: errorMessage
      })))
      
      onUploadError?.(errorMessage)
    }
  }, [
    challengeId,
    evidenceId,
    isPrivate,
    multiple,
    maxFiles,
    uploadFile,
    uploadMultipleFiles,
    onUploadComplete,
    onUploadError
  ])

  const handleDrop = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    setIsDragOver(false)
    
    const files = e.dataTransfer.files
    if (files.length > 0) {
      handleFileSelect(files)
    }
  }, [handleFileSelect])

  const handleDragOver = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    setIsDragOver(true)
  }, [])

  const handleDragLeave = useCallback((e: React.DragEvent) => {
    e.preventDefault()
    setIsDragOver(false)
  }, [])

  const handleInputChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files
    if (files && files.length > 0) {
      handleFileSelect(files)
    }
  }, [handleFileSelect])

  const removeFile = useCallback((index: number) => {
    setUploadingFiles(prev => prev.filter((_, i) => i !== index))
  }, [])

  const clearAll = useCallback(() => {
    setUploadingFiles([])
  }, [])

  const getFileIcon = (file: File) => {
    const type = file.type.toLowerCase()
    if (type.startsWith('image/')) return <Image className="w-4 h-4" />
    if (type.startsWith('video/')) return <Video className="w-4 h-4" />
    return <FileText className="w-4 h-4" />
  }

  const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }

  return (
    <div className={`w-full ${className}`}>
      {/* Drop Zone */}
      <div
        className={`relative border-2 border-dashed rounded-lg p-6 text-center transition-colors ${
          isDragOver
            ? 'border-blue-400 bg-blue-50'
            : 'border-gray-300 hover:border-gray-400'
        }`}
        onDrop={handleDrop}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
      >
        <input
          ref={fileInputRef}
          type="file"
          multiple={multiple}
          accept={accept.join(',')}
          onChange={handleInputChange}
          className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
        />
        
        <div className="space-y-2">
          <Upload className="mx-auto h-12 w-12 text-gray-400" />
          <div className="text-sm text-gray-600">
            <button
              type="button"
              className="font-medium text-blue-600 hover:text-blue-500"
              onClick={() => fileInputRef.current?.click()}
            >
              Click to upload
            </button>
            {' '}or drag and drop
          </div>
          <p className="text-xs text-gray-500">
            {accept.includes('image/*') && 'Images, '}
            {accept.includes('video/*') && 'Videos, '}
            {accept.includes('application/pdf') && 'PDFs '}
            up to {maxFiles} files
          </p>
        </div>
      </div>

      {/* Upload Progress */}
      {uploadingFiles.length > 0 && (
        <div className="mt-4 space-y-2">
          <div className="flex items-center justify-between">
            <h4 className="text-sm font-medium text-gray-900">
              Uploading {uploadingFiles.length} file{uploadingFiles.length > 1 ? 's' : ''}
            </h4>
            <button
              onClick={clearAll}
              className="text-xs text-gray-500 hover:text-gray-700"
            >
              Clear all
            </button>
          </div>

          <div className="space-y-2 max-h-60 overflow-y-auto">
            {uploadingFiles.map((item, index) => (
              <div
                key={`${item.file.name}-${index}`}
                className="flex items-center space-x-3 p-3 bg-gray-50 rounded-lg"
              >
                {/* File Icon */}
                <div className="flex-shrink-0">
                  {item.status === 'completed' ? (
                    <CheckCircle className="w-5 h-5 text-green-500" />
                  ) : item.status === 'error' ? (
                    <AlertCircle className="w-5 h-5 text-red-500" />
                  ) : (
                    getFileIcon(item.file)
                  )}
                </div>

                {/* File Info */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center justify-between">
                    <p className="text-sm font-medium text-gray-900 truncate">
                      {item.file.name}
                    </p>
                    <button
                      onClick={() => removeFile(index)}
                      className="flex-shrink-0 ml-2 text-gray-400 hover:text-gray-600"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  </div>
                  
                  <div className="flex items-center justify-between text-xs text-gray-500">
                    <span>{formatFileSize(item.file.size)}</span>
                    {item.status === 'uploading' && (
                      <span>{Math.round(item.progress)}%</span>
                    )}
                  </div>

                  {/* Progress Bar */}
                  {item.status === 'uploading' && (
                    <div className="mt-1 w-full bg-gray-200 rounded-full h-1">
                      <div
                        className="bg-blue-600 h-1 rounded-full transition-all duration-300"
                        style={{ width: `${item.progress}%` }}
                      />
                    </div>
                  )}

                  {/* Error Message */}
                  {item.status === 'error' && item.error && (
                    <p className="mt-1 text-xs text-red-600">{item.error}</p>
                  )}

                  {/* Success Message */}
                  {item.status === 'completed' && item.media && (
                    <p className="mt-1 text-xs text-green-600">
                      Upload complete
                    </p>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default MediaUpload
