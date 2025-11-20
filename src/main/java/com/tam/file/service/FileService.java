package com.tam.file.service;

import com.tam.file.dto.request.UploadFileRequest;
import com.tam.file.dto.response.FileData;
import com.tam.file.dto.response.FileMnmtResponse;
import com.tam.file.dto.response.FileResponse;
import com.tam.file.entity.FileMnmt;
import com.tam.file.exception.AppException;
import com.tam.file.exception.ErrorCode;
import com.tam.file.repository.FileMgmtRepository;
import com.tam.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@Service
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class FileService {
    FileRepository fileRepository;
    FileMgmtRepository fileMgmtRepository;

    public FileResponse uploadFile(MultipartFile file, UploadFileRequest request) throws IOException, AppException {
        String fileType = request.getType().toUpperCase();

        switch (fileType) {
            case "AVATAR":
                return handleUploadAvatar(file, request);
            case "WALLPAPER":
                return handleUploadWallpaper(file, request);
            case "POST":
                return handleUploadPostImage(file, request);
            case "STORY":
                return handleUploadStoryImage(file, request);
            case "MESSAGE":
                return handleUploadMessageImage(file, request);
            default:
                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    public FileResponse handleUploadAvatar(MultipartFile file, UploadFileRequest request) throws IOException {
        var fileInfo = fileRepository.storeToS3(file, request);

        FileMnmt fileMnmt = fileMgmtRepository.findById(request.getOwnerId())
                .orElse(FileMnmt.builder()
                        .id(request.getOwnerId())
                        .ownerId(request.getOwnerId())
                        .build());

        // Set tất cả avatar cũ thành inactive
        fileMnmt.getAvatar().forEach(avatar -> avatar.setActive(false));

        FileMnmt.FileDetail avatarDetail = FileMnmt.FileDetail.builder()
                .contentType(file.getContentType())
                .size(file.getSize())
                .md5Checksum(fileInfo.getMd5Checksum())
                .path(fileInfo.getPath())
                .url(fileInfo.getUrl())
                .isActive(true)
                .uploadedAt(Instant.now().toEpochMilli())
                .build();

        fileMnmt.getAvatar().add(avatarDetail);
        fileMgmtRepository.save(fileMnmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

    public FileResponse handleUploadWallpaper(MultipartFile file, UploadFileRequest request) throws IOException {
        var fileInfo = fileRepository.storeToS3(file, request);

        FileMnmt fileMnmt = fileMgmtRepository.findById(request.getOwnerId())
                .orElse(FileMnmt.builder()
                        .id(request.getOwnerId())
                        .ownerId(request.getOwnerId())
                        .build());

        // Set tất cả wallpaper cũ thành inactive
        fileMnmt.getWallpaper().forEach(wallpaper -> wallpaper.setActive(false));

        FileMnmt.FileDetail wallpaperDetail = FileMnmt.FileDetail.builder()
                .contentType(file.getContentType())
                .size(file.getSize())
                .md5Checksum(fileInfo.getMd5Checksum())
                .path(fileInfo.getPath())
                .url(fileInfo.getUrl())
                .isActive(true)
                .uploadedAt(Instant.now().toEpochMilli())
                .build();

        fileMnmt.getWallpaper().add(wallpaperDetail);
        fileMgmtRepository.save(fileMnmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

    public FileResponse handleUploadPostImage(MultipartFile file, UploadFileRequest request) throws IOException, AppException {
        if (request.getPostId() == null || request.getPostId().isEmpty()) {
            throw new AppException(ErrorCode.POST_ID_REQUIRED);
        }

        var fileInfo = fileRepository.storeToS3(file, request);

        FileMnmt fileMnmt = fileMgmtRepository.findById(request.getOwnerId())
                .orElse(FileMnmt.builder()
                        .id(request.getOwnerId())
                        .ownerId(request.getOwnerId())
                        .build());

        FileMnmt.PostFileDetail postDetail = FileMnmt.PostFileDetail.builder()
                .postId(request.getPostId())
                .contentType(file.getContentType())
                .size(file.getSize())
                .md5Checksum(fileInfo.getMd5Checksum())
                .path(fileInfo.getPath())
                .url(fileInfo.getUrl())
                .isActive(true)
                .uploadedAt(Instant.now().toEpochMilli())
                .build();

        fileMnmt.getPost().add(postDetail);
        fileMgmtRepository.save(fileMnmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

    public FileResponse handleUploadStoryImage(MultipartFile file, UploadFileRequest request) throws IOException, AppException {
        if (request.getStoryId() == null || request.getStoryId().isEmpty()) {
            throw new AppException(ErrorCode.STORY_ID_REQUIRED);
        }

        var fileInfo = fileRepository.storeToS3(file, request);

        FileMnmt fileMnmt = fileMgmtRepository.findById(request.getOwnerId())
                .orElse(FileMnmt.builder()
                        .id(request.getOwnerId())
                        .ownerId(request.getOwnerId())
                        .build());

        FileMnmt.StoryFileDetail storyDetail = FileMnmt.StoryFileDetail.builder()
                .storyId(request.getStoryId())
                .contentType(file.getContentType())
                .size(file.getSize())
                .md5Checksum(fileInfo.getMd5Checksum())
                .path(fileInfo.getPath())
                .url(fileInfo.getUrl())
                .isActive(true)
                .uploadedAt(Instant.now().toEpochMilli())
                .build();

        fileMnmt.getStory().add(storyDetail);
        fileMgmtRepository.save(fileMnmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

    public FileResponse handleUploadMessageImage(MultipartFile file, UploadFileRequest request) throws IOException, AppException {
        if (request.getMessageId() == null || request.getMessageId().isEmpty()) {
            throw new AppException(ErrorCode.MESSAGE_ID_REQUIRED);
        }

        var fileInfo = fileRepository.storeToS3(file, request);

        FileMnmt fileMnmt = fileMgmtRepository.findById(request.getOwnerId())
                .orElse(FileMnmt.builder()
                        .id(request.getOwnerId())
                        .ownerId(request.getOwnerId())
                        .build());

        FileMnmt.MessageFileDetail messageDetail = FileMnmt.MessageFileDetail.builder()
                .messageId(request.getMessageId())
                .contentType(file.getContentType())
                .size(file.getSize())
                .md5Checksum(fileInfo.getMd5Checksum())
                .path(fileInfo.getPath())
                .url(fileInfo.getUrl())
                .isActive(true)
                .uploadedAt(Instant.now().toEpochMilli())
                .build();

        fileMnmt.getMessage().add(messageDetail);
        fileMgmtRepository.save(fileMnmt);

        return FileResponse.builder()
                .originalFileName(file.getOriginalFilename())
                .url(fileInfo.getUrl())
                .build();
    }

    /**
     * Thay đổi trạng thái isActive dựa trên URL, type và ownerId
     * URL đóng vai trò như UUID để định danh file
     *
     * Logic:
     * - AVATAR, WALLPAPER: Tất cả file khác sẽ set thành false, chỉ file được chọn là true
     * - POST, STORY, MESSAGE: Giữ nguyên logic giống Facebook (có thể có nhiều file active)
     *
     * @param ownerId ID chủ sở hữu file
     * @param type Loại file (AVATAR, WALLPAPER, POST, STORY, MESSAGE)
     * @param url URL của file (dùng để tìm file)
     * @param isActive Trạng thái mới
     */
    public void updateFileActiveStatus(String ownerId, String type, String url, boolean isActive) {
        FileMnmt fileMnmt = fileMgmtRepository.findById(ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        String fileType = type.toUpperCase();
        boolean found = false;

        switch (fileType) {
            case "AVATAR":
                found = updateAvatarStatus(fileMnmt, url, isActive);
                break;
            case "WALLPAPER":
                found = updateWallpaperStatus(fileMnmt, url, isActive);
                break;
            case "POST":
                found = updatePostFileDetailStatus(fileMnmt.getPost(), url, isActive);
                break;
            case "STORY":
                found = updateStoryFileDetailStatus(fileMnmt.getStory(), url, isActive);
                break;
            case "MESSAGE":
                found = updateMessageFileDetailStatus(fileMnmt.getMessage(), url, isActive);
                break;
            default:
                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        if (!found) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }

        fileMgmtRepository.save(fileMnmt);
    }

    /**
     * Cập nhật avatar: set tất cả thành false, chỉ file được chọn là true
     */
    private boolean updateAvatarStatus(FileMnmt fileMnmt, String url, boolean isActive) {
        boolean found = false;

        for (FileMnmt.FileDetail avatar : fileMnmt.getAvatar()) {
            if (avatar.getUrl().equals(url)) {
                avatar.setActive(isActive);
                found = true;
            } else if (isActive) {
                // Nếu set file này thành active, set các cái khác thành false
                avatar.setActive(false);
            }
        }

        return found;
    }

    /**
     * Cập nhật wallpaper: set tất cả thành false, chỉ file được chọn là true
     */
    private boolean updateWallpaperStatus(FileMnmt fileMnmt, String url, boolean isActive) {
        boolean found = false;

        for (FileMnmt.FileDetail wallpaper : fileMnmt.getWallpaper()) {
            if (wallpaper.getUrl().equals(url)) {
                wallpaper.setActive(isActive);
                found = true;
            } else if (isActive) {
                // Nếu set file này thành active, set các cái khác thành false
                wallpaper.setActive(false);
            }
        }

        return found;
    }

    /**
     * Cập nhật post: giữ logic Facebook (có thể có nhiều post active)
     */
    private boolean updatePostFileDetailStatus(java.util.List<FileMnmt.PostFileDetail> files, String url, boolean isActive) {
        return files.stream()
                .filter(f -> f.getUrl().equals(url))
                .findFirst()
                .map(f -> {
                    f.setActive(isActive);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Cập nhật story: giữ logic Facebook (có thể có nhiều story active)
     */
    private boolean updateStoryFileDetailStatus(java.util.List<FileMnmt.StoryFileDetail> files, String url, boolean isActive) {
        return files.stream()
                .filter(f -> f.getUrl().equals(url))
                .findFirst()
                .map(f -> {
                    f.setActive(isActive);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Cập nhật message: giữ logic Facebook (có thể có nhiều message active)
     */
    private boolean updateMessageFileDetailStatus(java.util.List<FileMnmt.MessageFileDetail> files, String url, boolean isActive) {
        return files.stream()
                .filter(f -> f.getUrl().equals(url))
                .findFirst()
                .map(f -> {
                    f.setActive(isActive);
                    return true;
                })
                .orElse(false);
    }

    public FileData download(String filename) throws IOException {
        throw new UnsupportedOperationException("Download method needs to be refactored");
    }

    // get file
    // Lấy tất cả file của user
    public Optional<FileMnmt> getAllFileByOwnerId(String ownerId) {
        return fileMgmtRepository.findByOwnerId(ownerId);
    }

    // Lấy file theo type (avatar, wallpaper, post, story, message)
    public FileMnmt getAllFilesByOwnerIdAndType(String ownerId, String type) {
        Optional<FileMnmt> fileMnmt = fileMgmtRepository.findByOwnerId(ownerId);

        if (fileMnmt.isEmpty()) {
            return null;
        }

        FileMnmt result = fileMnmt.get();
        FileMnmt filtered = new FileMnmt();
        filtered.setId(result.getId());
        filtered.setOwnerId(result.getOwnerId());

        switch (type.toLowerCase()) {
            case "avatar":
                filtered.setAvatar(result.getAvatar());
                break;
            case "wallpaper":
                filtered.setWallpaper(result.getWallpaper());
                break;
            case "post":
                filtered.setPost(result.getPost());
                break;
            case "story":
                filtered.setStory(result.getStory());
                break;
            case "message":
                filtered.setMessage(result.getMessage());
                break;
            default:
                return null;
        }

        return filtered;
    }
}