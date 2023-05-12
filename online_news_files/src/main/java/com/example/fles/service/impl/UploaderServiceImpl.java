package com.example.fles.service.impl;

import com.example.fles.service.UploaderService;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploaderServiceImpl implements UploaderService {

    @Autowired
    FastFileStorageClient fastFileStorageClient;

    @Override
    public String upload(MultipartFile file,String fileExtname)throws Exception {
        StorePath storePath = fastFileStorageClient.uploadFile (file.getInputStream ( ), file.getSize ( ), fileExtname, null);
        return storePath.getFullPath ();
    }
}
