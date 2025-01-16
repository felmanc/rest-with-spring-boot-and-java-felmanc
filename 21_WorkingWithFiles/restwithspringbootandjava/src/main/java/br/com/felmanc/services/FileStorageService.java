package br.com.felmanc.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.felmanc.config.FileStorageConfig;
import br.com.felmanc.exceptions.FileStorageException;

@Service
public class FileStorageService {

	// Path completo
	private final Path fileStorageLocation;

	//@Autowired
	public FileStorageService(FileStorageConfig fileStorageConfig) {
		// Normaliza e transforma em um java.nio.file.Path
		Path path = Paths.get(fileStorageConfig.getUploadDir())
				.toAbsolutePath().normalize();
		
		this.fileStorageLocation = path;
		
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception e) {
			throw new FileStorageException(
				"Could not create the directory where the uploaded files will be stored!", e);
		}
	}
	
	public String storeFile(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
			
		try {
			if(filename.contains("..")) {
				throw new FileStorageException(
					"Sorry! Filename contains invalid path sequence " + filename);
			}
			// Cria o arquivo vazio no diretório
			Path targetLocation = this.fileStorageLocation.resolve(filename);
			// InputStream: Array de bytes do Multipart files
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return filename;
		} catch (Exception e) {
			throw new FileStorageException(
					"Could not store file " + filename + ". Please try again!", e);
		}
	}
	
	
}
