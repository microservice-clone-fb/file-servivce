package com.tam.file.repository;

import com.tam.file.dto.FileInfo;
import com.tam.file.dto.request.UploadFileRequest;
import com.tam.file.entity.FileMnmt;
import com.tam.file.exception.AppException;
import com.tam.file.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FileRepository {
    @Value("${app.file.storage-dir}")
    private String storageDir;

    @Value("${app.file.download-prefix}")
    private String urlPrefix;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private S3Client s3Client;

    public FileRepository(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // LOCAL
    public FileInfo storeToLocal(MultipartFile file, UploadFileRequest request) {
        Path folder = Paths.get(storageDir);

        String fileExtension = StringUtils
                .getFilenameExtension(file.getOriginalFilename());

        String fileName = Objects.isNull(fileExtension)
                ? UUID.randomUUID().toString()
                : UUID.randomUUID() + "." + fileExtension;

        Path filePath = folder.resolve(fileName).normalize().toAbsolutePath();

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return FileInfo.builder()
                    .name(fileName)
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .md5Checksum(DigestUtils.md5DigestAsHex(file.getInputStream()))
                    .path(filePath.toString())
                    .url(urlPrefix + fileName)
                    .build();
        }
        catch (NoSuchFileException e) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        catch (FileAlreadyExistsException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        catch (IOException e) {
            throw new AppException(ErrorCode.FILE_NOT_UPLOADED_CORRECTLY);
        }
    }

    public Resource readFromLocal(String path) throws IOException {
        // doc file tu path
        var data = Files.readAllBytes(Path.of(path));
        return new ByteArrayResource(data);
    }

    // S3
    public FileInfo storeToS3(MultipartFile file, UploadFileRequest request) {
        try {
            // Xác định prefix theo loại file từ request type
            String prefix = getPrefixByType(request.getType());
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            String key = prefix + "/" + fileName;

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(file.getBytes()));

            return FileInfo.builder()
                    .name(fileName)
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .md5Checksum(DigestUtils.md5DigestAsHex(file.getInputStream()))
                    .path(key)
                    .url("https://" + bucketName + ".s3.ap-southeast-1.amazonaws.com/" + key)
                    .build();

        } catch (S3Exception e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_NOT_UPLOADED_CORRECTLY);
        }
    }

    public Resource readFromS3(String key) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key) // path ở đây chính là key đã lưu
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object =
                    s3Client.getObject(getObjectRequest);

            byte[] data = s3Object.readAllBytes();
            return new ByteArrayResource(data);
        } catch (NoSuchKeyException e) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        } catch (S3Exception | IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Xác định prefix folder trên S3 dựa vào type từ request
     * AVATAR, WALLPAPER -> avatars/
     * POST -> posts/
     * STORY -> stories/
     * MESSAGE -> messages/
     */
    private String getPrefixByType(String type) {
        if (type == null) return "others";

        switch (type.toUpperCase()) {
            case "AVATAR":
            case "WALLPAPER":
                return "avatars";
            case "POST":
                return "posts";
            case "STORY":
                return "stories";
            case "MESSAGE":
                return "messages";
            default:
                return "others";
        }
    }

    /**
     * Xác định prefix dựa vào content type của file (backup method)
     */
    private String getPrefixByContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return "others";

        if (contentType.startsWith("image/")) {
            return "images";
        } else if (contentType.startsWith("video/")) {
            return "videos";
        } else if (contentType.equals("application/pdf")
                || contentType.equals("application/msword")
                || contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return "documents";
        } else {
            return "others";
        }
    }
}