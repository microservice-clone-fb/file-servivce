package com.tam.file.repository;

import com.tam.file.entity.FileMnmt;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMgmtRepository extends MongoRepository<FileMnmt, String> {
}
