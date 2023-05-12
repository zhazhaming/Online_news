package com.example.api.controller.files;

import com.example.CommonResponse.CommonResponse;
import com.example.pojo.bo.NewAdminBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Api(value = "上传文件controller",tags = {"UploadController的功能"})
@RequestMapping("fs")
public interface UploadControllerAPI {
    @ApiOperation (value = "上传文件接口测试",notes = "上传文件接口测试")
    @RequestMapping("/uploadFace")
    public CommonResponse uploadFace(@RequestParam String userId,
                                      MultipartFile file) throws Exception;

    @PostMapping("/uploadSomeFiles")
    public CommonResponse uploadSomeFiles(@RequestParam String userId,
                                           MultipartFile[] files) throws Exception;


    @PostMapping("/uploadToGridFS")
    public CommonResponse uploadToGridFS(@RequestBody NewAdminBO newAdminBO)
            throws Exception;

    /**
     * 从gridfs中读取图片内容
     * @param faceId
     * @return
     * @throws Exception
     */
    @GetMapping("/readInGridFS")
    public void readInGridFS(String faceId,HttpServletRequest request,HttpServletResponse response) throws Exception;

    @GetMapping("/readFace64InGridFS")
    public CommonResponse readFace64InGridFS(String faceId,
                                             HttpServletRequest request,
                                             HttpServletResponse response)throws Exception;
}
