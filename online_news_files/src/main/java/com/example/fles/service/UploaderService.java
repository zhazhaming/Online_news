package com.example.fles.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploaderService {

    public String upload(MultipartFile file, String fileExtname) throws Exception;
}
