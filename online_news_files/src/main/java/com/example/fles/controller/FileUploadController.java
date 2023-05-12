package com.example.fles.controller;

import com.example.CommonException.CommonException;
import com.example.CommonResponse.CommonResponse;
import com.example.CommonResponse.ResponseEnum;
import com.example.api.controller.files.UploadControllerAPI;
import com.example.fles.resource.FileResource;
import com.example.fles.service.UploaderService;
import com.example.pojo.bo.NewAdminBO;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.utilis.FileUtils;

@RestController
public class FileUploadController implements UploadControllerAPI {
    final static Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private UploaderService uploaderService;

    @Autowired
    private FileResource fileResource;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Override
    public CommonResponse uploadFace(String userId, MultipartFile file) throws Exception {
        String path ="";
        if (file!=null){
            String fileName = file.getOriginalFilename ();
            if (StringUtils.isNotBlank(fileName)){
                String fileNameArr[] = fileName.split("\\.");
                String suffix = fileNameArr[fileNameArr.length - 1];
                if (!suffix.equalsIgnoreCase("png") &&
                        !suffix.equalsIgnoreCase("jpg") &&
                        !suffix.equalsIgnoreCase("jpeg")
                ) {
                    return CommonResponse.errorCustom(ResponseEnum.FILE_FORMATTER_FAILD);
                }
                path = uploaderService.upload (file, suffix);
            }else {
                return CommonResponse.errorCustom (ResponseEnum.FILE_FORMATTER_FAILD);
            }
        }else {
            return CommonResponse.errorCustom(ResponseEnum.FILE_UPLOAD_NULL_ERROR);
        }

        String FinallPath = "";
        if (StringUtils.isNotBlank (path)){
            if (fileResource.getHost ()!=null){
                FinallPath = fileResource.getHost ()+path;
            }else {
                return CommonResponse.errorCustom (ResponseEnum.FILE_UPLOAD_FAILD);
            }
        }else {
            return CommonResponse.errorCustom (ResponseEnum.FILE_UPLOAD_FAILD);
        }
        logger.info("path = " + FinallPath);
        System.out.println (FinallPath );
        return CommonResponse.ok (FinallPath);
    }

    @Override
    public CommonResponse uploadSomeFiles(String userId, MultipartFile[] files) throws Exception {
        List<String> imgUrlList = new ArrayList<> (  );
        if (files!=null && files.length>0){
            //上传多张图片
            for (MultipartFile file: files) {
                String path = "";
                if (file!=null){
                    String fileName = file.getOriginalFilename ();
                    if (StringUtils.isNotBlank(fileName)){
                        String fileNameArr[] = fileName.split("\\.");
                        String suffix = fileNameArr[fileNameArr.length - 1];
                        if (!suffix.equalsIgnoreCase("png") &&
                                !suffix.equalsIgnoreCase("jpg") &&
                                !suffix.equalsIgnoreCase("jpeg")
                        ) {
                            continue;
                        }
                        path = uploaderService.upload (file, suffix);
                    }else {
                        continue;
                    }
                }else {
                    continue;
                }

                String FinallPath = "";
                if (StringUtils.isNotBlank (path)){
                    if (fileResource.getHost ()!=null){
                        FinallPath = fileResource.getHost ()+path;
                        imgUrlList.add (FinallPath);
                    }else {
                        continue;
                    }
                }else {
                    continue;
                }
                logger.info("path = " + FinallPath);
            }
        }
        return CommonResponse.ok (imgUrlList);
    }

    @Override
    public CommonResponse uploadToGridFS(NewAdminBO newAdminBO) throws Exception {
        // 获得图片的base64字符串
        String file64 = newAdminBO.getImg64();

        // 将base64字符串转换为byte数组
        byte[] bytes = new BASE64Decoder ().decodeBuffer(file64.trim());

        // 转换为输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

        // 上传到gridfs中
        ObjectId fileId = gridFSBucket.uploadFromStream(newAdminBO.getUsername() + ".png", inputStream);

        // 获得文件在gridfs中的主键id
        String fileIdStr = fileId.toString();

        return CommonResponse.ok(fileIdStr);
    }

    @Override
    public void readInGridFS(String faceId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if(StringUtils.isBlank (faceId)|| faceId.equalsIgnoreCase ("null")){
            CommonException.display (ResponseEnum.FILE_NOT_EXIST_ERROR);
        }
        // 1. 从gridfs中读取
        File adminFace = readGridFSByFaceId(faceId);

        // 2. 把人脸图片输出到浏览器
        FileUtils.downloadFileByStream(response, adminFace);
    }

    // 工具类
    private File readGridFSByFaceId(String faceId) throws Exception{
        GridFSFindIterable gridFSFiles
                = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));
        GridFSFile gridFS = gridFSFiles.first();
        if (gridFS == null) {
            CommonException.display(ResponseEnum.FILE_NOT_EXIST_ERROR);
        }

        String fileName = gridFS.getFilename();
        System.out.println(fileName);

        // 获取文件流，保存文件到本地或者服务器的临时目录
        File fileTemp = new File("/workspace/temp_face");
        if (!fileTemp.exists()) {
            fileTemp.mkdirs();
        }

        File myFile = new File("/workspace/temp_face/" + fileName);

        // 创建文件输出流
        OutputStream os = new FileOutputStream (myFile);
        // 下载到服务器或者本地
        gridFSBucket.downloadToStream(new ObjectId(faceId), os);

        return myFile;
    }

    @Override
    public CommonResponse readFace64InGridFS(String faceId,
                                              HttpServletRequest request,
                                              HttpServletResponse response)
            throws Exception {

        // 0. 获得gridfs中人脸文件
        File myface = readGridFSByFaceId(faceId);

        // 1. 转换人脸为base64
        String base64Face = FileUtils.fileToBase64(myface);

        return CommonResponse.ok(base64Face);
    }
}
