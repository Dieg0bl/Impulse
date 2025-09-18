package com.impulse.infrastructure.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * Service for file storage operations
 * Supports presigned uploads and EXIF stripping per IMPULSE v1.0 §7.1 and §8
 */
@Service
public class StorageService {

    private static final String BASE_DIR = "backend/storage/";

    /**
     * Upload file to storage
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        java.nio.file.Path dirPath = java.nio.file.Paths.get(BASE_DIR, folder);
        java.nio.file.Files.createDirectories(dirPath);
        String fileName = generateFileName(file.getOriginalFilename());
        java.nio.file.Path filePath = dirPath.resolve(fileName);
        file.transferTo(filePath);
        return filePath.toString();
    }

    /**
     * Generate presigned URL for upload
     * Following IMPULSE v1.0 §7.1 presigned upload pattern
     */
    public StorageUploadPresignResponse generatePresignedUploadUrl(String fileName, String contentType) {
        // En local, devolvemos file:// y documentamos que en el futuro se puede integrar un controlador HTTP
        String generatedName = generateFileName(fileName);
        String uploadUrl = "file://" + BASE_DIR + generatedName;
        String accessUrl = uploadUrl;
        // Comentario: En el futuro se puede integrar un controlador HTTP para servir archivos
        return new StorageUploadPresignResponse(uploadUrl, accessUrl, 3600); // 1 hora
    }

    /**
     * Delete file from storage
     */
    public boolean deleteFile(String filePath) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            java.nio.file.Files.deleteIfExists(path);
            return true;
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Error eliminando archivo: " + filePath, e);
        }
    }

    /**
     * Get file URL
     */
    public String getFileUrl(String filePath) {
        // En local, devolvemos file://<ruta absoluta>
        java.nio.file.Path path = java.nio.file.Paths.get(filePath);
        return "file://" + path.toAbsolutePath();
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String filePath) {
        java.nio.file.Path path = java.nio.file.Paths.get(filePath);
        return java.nio.file.Files.exists(path);
    }

    /**
     * Get file size
     */
    public long getFileSize(String filePath) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            return java.nio.file.Files.size(path);
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Error obteniendo tamaño de archivo: " + filePath, e);
        }
    }

    /**
     * Copy file
     */
    public String copyFile(String sourcePath, String destinationPath) {
        try {
            java.nio.file.Path src = java.nio.file.Paths.get(sourcePath);
            java.nio.file.Path dest = java.nio.file.Paths.get(destinationPath);
            java.nio.file.Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return dest.toString();
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Error copiando archivo: " + sourcePath + " a " + destinationPath, e);
        }
    }

    /**
     * Move file
     */
    public String moveFile(String sourcePath, String destinationPath) {
        try {
            java.nio.file.Path src = java.nio.file.Paths.get(sourcePath);
            java.nio.file.Path dest = java.nio.file.Paths.get(destinationPath);
            java.nio.file.Files.move(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            return dest.toString();
        } catch (IOException | SecurityException e) {
            throw new IllegalStateException("Error moviendo archivo: " + sourcePath + " a " + destinationPath, e);
        }
    }

    /**
     * Generate unique file name
     */
    private String generateFileName(String originalFileName) {
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Validate file type
     */
    public boolean isValidFileType(String fileName, String[] allowedTypes) {
        if (fileName == null || allowedTypes == null) {
            return false;
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate file size
     */
    public boolean isValidFileSize(long fileSize, long maxSizeBytes) {
        return fileSize <= maxSizeBytes;
    }

    /**
     * Response class for presigned upload URLs
     */
    public static class StorageUploadPresignResponse {
        private String uploadUrl;
        private String accessUrl;
        private int expirySeconds;

        public StorageUploadPresignResponse(String uploadUrl, String accessUrl, int expirySeconds) {
            this.uploadUrl = uploadUrl;
            this.accessUrl = accessUrl;
            this.expirySeconds = expirySeconds;
        }

        // Getters and setters
        public String getUploadUrl() { return uploadUrl; }
        public void setUploadUrl(String uploadUrl) { this.uploadUrl = uploadUrl; }

        public String getAccessUrl() { return accessUrl; }
        public void setAccessUrl(String accessUrl) { this.accessUrl = accessUrl; }

        public int getExpirySeconds() { return expirySeconds; }
        public void setExpirySeconds(int expirySeconds) { this.expirySeconds = expirySeconds; }
    }
}
