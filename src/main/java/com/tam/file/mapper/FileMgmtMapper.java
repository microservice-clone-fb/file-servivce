package com.tam.file.mapper;

import com.tam.file.dto.FileInfo;
import com.tam.file.entity.FileMnmt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FileMgmtMapper {
    @Mapping(target = "id", source = "name")
    FileMnmt toFileMnmt(FileInfo fileInfo);
}
