package com.tam.file.repository;

import com.tam.file.entity.FileMnmt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileMgmtRepository extends MongoRepository<FileMnmt, String> {
    // Lấy tất cả file của user theo ownerId
    Optional<FileMnmt> findByOwnerId(String ownerId);

    // Query custom để lấy file theo type cụ thể
    @Query("{ 'ownerId': ?0 }")
    Optional<FileMnmt> findFilesByOwnerId(String ownerId);
}
