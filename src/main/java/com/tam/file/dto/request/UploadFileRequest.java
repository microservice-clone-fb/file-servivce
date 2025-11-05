package com.tam.file.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UploadFileRequest {
    String ownerId;
    String type; // AVATAR, WALLPAPER, POST, STORY, MESSAGE
    String postId; // Optional - chỉ dùng cho POST
    String storyId; // Optional - chỉ dùng cho STORY
    String messageId; // Optional - chỉ dùng cho MESSAGE
}