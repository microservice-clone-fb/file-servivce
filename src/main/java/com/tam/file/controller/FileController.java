package com.tam.file.controller;

import com.tam.file.dto.ApiResponse;
import com.tam.file.dto.request.UploadFileRequest;
import com.tam.file.dto.response.FileResponse;
import com.tam.file.entity.FileMnmt;
import com.tam.file.exception.AppException;
import com.tam.file.service.FileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileController {
    FileService fileService;

    @PostMapping("/media/upload")
    public ApiResponse<FileResponse> uploadMedia(
            @RequestPart("file") MultipartFile file,           // Đổi từ @RequestParam thành @RequestPart
            @RequestPart("request") UploadFileRequest request  // Đổi từ @RequestBody thành @RequestPart
    ) throws IOException, AppException {
        return ApiResponse.<FileResponse>builder()
                .result(fileService.uploadFile(file, request))
                .build();
    }

    @GetMapping("/media/download/{fileName}")
    ResponseEntity<Resource> downloadMedia(@PathVariable String fileName) throws IOException {
        var fileData = fileService.download(fileName);

        return ResponseEntity.<Resource>ok()
                .header(HttpHeaders.CONTENT_TYPE, fileData.contentType())
                .body(fileData.resource());
    }

    @GetMapping("/media/view/all-with-type/{userId}/{type}")       // laasy ảnh bơi type vd avatar, wallpaper,...
    public ApiResponse<FileMnmt> getAllFileWithTypeAndUserId(@PathVariable String userId, @PathVariable String type){
        FileMnmt fileMnmt = fileService.getAllFilesByOwnerIdAndType(userId, type);

        return ApiResponse.<FileMnmt>builder()
                .result(fileMnmt)
                .build();
    }

    @GetMapping("/media/view/all/{userId}")    // laasy full anh cua user
    ApiResponse<FileMnmt> getAllFileByUserId(@PathVariable String userId){
        Optional<FileMnmt> response = fileService.getAllFileByOwnerId(userId);
        return ApiResponse.<FileMnmt>builder()
                .result(response.get())
                .build();
    }
}
