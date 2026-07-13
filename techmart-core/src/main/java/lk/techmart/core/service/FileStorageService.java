package lk.techmart.core.service;

import jakarta.ejb.Remote;
import lk.techmart.core.dto.FileItem;

import java.io.InputStream;

@Remote
public interface FileStorageService {
    FileItem saveFile(byte[] fileData, String originalFileName, String folder);
}
