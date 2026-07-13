package lk.techmart.ejb.beans;

import jakarta.ejb.Stateless;
import lk.techmart.core.dto.FileItem;
import lk.techmart.core.service.FileStorageService;
import lk.techmart.core.util.AppConfig;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Stateless
public class FileStorageServiceBean implements FileStorageService {
    @Override
    public FileItem saveFile(byte[] fileData, String originalFileName, String folder) {
        try{
            if (fileData == null || fileData.length == 0) {
                throw new RuntimeException("File is empty");
            }

            if (fileData.length > 5 * 1024 * 1024) {
                throw new RuntimeException("File exceeds 5MB");
            }

            if (originalFileName == null ||
                    !originalFileName.contains(".")) {
                throw new RuntimeException("Invalid file name");
            }

            String extension = originalFileName.substring(
                            originalFileName.lastIndexOf('.') + 1).toLowerCase();

            Set<String> allowed =
                    Set.of("jpg","jpeg","png","webp");

            if(!allowed.contains(extension.toLowerCase())){
                throw new RuntimeException("Invalid file type");
            }

            String fileName = UUID.randomUUID() + "." + extension;

            Path uploadPath = Paths.get(AppConfig.UPLOAD_DIR,folder);
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);

            Files.write(filePath, fileData);

            return FileItem.builder()
                    .fileName(fileName)
                    .originalName(originalFileName)
                    .path(filePath.toString())
                    .url("/uploads/"+folder+"/"+fileName)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
