package com.tam.file.controller;

import com.tam.file.dto.ApiResponse;
import com.tam.file.dto.request.UploadFileRequest;
import com.tam.file.dto.response.FileResponse;
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

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InternalFileController {
    FileService fileService;

    @PostMapping("/internal/media/upload")
    public ApiResponse<FileResponse> uploadMedia(
            @RequestPart("file") MultipartFile file,
            @RequestPart("request") UploadFileRequest request
    ) throws IOException, AppException {
        return ApiResponse.<FileResponse>builder()
                .result(fileService.uploadFile(file, request))
                .build();
    }

    @GetMapping("/internal/media/download/{fileName}")
    ResponseEntity<Resource> downloadMedia(@PathVariable String fileName) throws IOException {
        var fileData = fileService.download(fileName);

        return ResponseEntity.<Resource>ok()
                .header(HttpHeaders.CONTENT_TYPE, fileData.contentType())
                .body(fileData.resource());
    }
//    @DeleteMapping("/internal/media/{fileName}")
//    public ApiResponse<Void> deleteMedia(@PathVariable String fileName) throws IOException {
//        fileService.delete(fileName);
//        return ApiResponse.<Void>builder()
//                .build();
//    }
}