package com.tam.file.dto.response;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class FileMnmtResponse {
    @JsonProperty("_id")
    String id;

    String ownerId;

    @Builder.Default
    List<FileDetailDto> avatar = new ArrayList<>();

    @Builder.Default
    List<FileDetailDto> wallpaper = new ArrayList<>();

    @Builder.Default
    List<PostFileDetailDto> post = new ArrayList<>();

    @Builder.Default
    List<StoryFileDetailDto> story = new ArrayList<>();

    @Builder.Default
    List<MessageFileDetailDto> message = new ArrayList<>();

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileDetailDto {
        String contentType;
        long size;
        String md5Checksum;
        String path;
        String url;
        boolean isActive;
        long uploadedAt;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class PostFileDetailDto extends FileDetailDto {
        String postId;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class StoryFileDetailDto extends FileDetailDto {
        String storyId;
    }

    @Data
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = true)
    public static class MessageFileDetailDto extends FileDetailDto {
        String messageId;
    }
}