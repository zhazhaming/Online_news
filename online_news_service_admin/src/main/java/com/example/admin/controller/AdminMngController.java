package com.example.admin.controller;

import com.example.CommonException.CommonException;
import com.example.CommonResponse.CommonResponse;
import com.example.CommonResponse.ResponseEnum;
import com.example.admin.service.AdminUserService;
import com.example.api.BaseController;
import com.example.api.controller.admin.AdminMngControllerAPI;
import com.example.enums.FaceVerifyType;
import com.example.pojo.AdminUser;
import com.example.pojo.bo.AdminLoginBO;
import com.example.pojo.bo.NewAdminBO;
import com.example.utilis.FaceUtils;
import com.example.utilis.FaceVerifyUtils;
import com.example.utilis.PagedGridResult;
import com.example.utilis.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
public class AdminMngController extends BaseController implements AdminMngControllerAPI {
    final static Logger logger = LoggerFactory.getLogger(AdminMngController.class);

    @Autowired
    private RedisOperator redis;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FaceVerifyUtils faceVerifyUtils;

    @Autowired
    private FaceUtils faceUtils;

    @Value ("${website.domain-name}")
    private String domain;

    @Value ("${website.domain-name}")
    private String host;

    @Override
    public CommonResponse adminLogin(AdminLoginBO adminLoginBO, HttpServletRequest request, HttpServletResponse response) {

        //判断管理员的用户名和密码不能位空
        if (StringUtils.isBlank (adminLoginBO.getUsername ())){
            return CommonResponse.errorCustom (ResponseEnum.ADMIN_USERNAME_NULL_ERROR);
        }
        if (StringUtils.isBlank (adminLoginBO.getPassword ())){
            return CommonResponse.errorCustom (ResponseEnum.ADMIN_PASSWORD_NULL_ERROR);
        }

        //用户是否存在
        AdminUser adminUser = adminUserService.queryAdminByUsername (adminLoginBO.getUsername ());
        if (adminUser==null){
            return CommonResponse.errorCustom (ResponseEnum.ADMIN_NOT_EXIT_ERROR);
        }
        boolean isPwdMatch = BCrypt.checkpw(adminLoginBO.getPassword(), adminUser.getPassword());
        if (isPwdMatch) {
            doLoginSettings(adminUser, request, response);
            return CommonResponse.ok();
        } else {
            return CommonResponse.errorCustom(ResponseEnum.ADMIN_NOT_EXIT_ERROR);
        }
    }

    private void doLoginSettings(AdminUser adminUser,HttpServletRequest request, HttpServletResponse response){
        // 保存token放入到redis中
        String token = UUID.randomUUID().toString();
        redis.set(REDIS_ADMIN_TOKEN + ":" + adminUser.getId(), token);

        // 保存admin登录基本token信息到cookie中
        setCookie(request, response, "atoken", token, COOKIE_MONTH,domain);
        setCookie(request, response, "aid", adminUser.getId(), COOKIE_MONTH,domain);
        setCookie(request, response, "aname", adminUser.getAdminName(), COOKIE_MONTH,domain);
    }

    @Override
    public CommonResponse adminIsExist(String username) {
        checkAdminExist (username);
        return CommonResponse.ok ();
    }

    private void checkAdminExist(String username) {
        AdminUser admin = adminUserService.queryAdminByUsername(username);
        if (admin != null) {
            CommonException.display(ResponseEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
    }

    @Override
    public CommonResponse addNewAdmin(NewAdminBO newAdminBO, HttpServletRequest request, HttpServletResponse response) {

        //判断新添加的管理员的用户名和密码不能位空
        if (StringUtils.isBlank (newAdminBO.getUsername ())){
            return CommonResponse.errorCustom (ResponseEnum.ADMIN_USERNAME_NULL_ERROR);
        }
        if (StringUtils.isBlank (newAdminBO.getPassword ())){
            return CommonResponse.errorCustom (ResponseEnum.ADMIN_PASSWORD_NULL_ERROR);
        }

        //  base64不为空，则代表人脸入库，否则需要用户输入密码和确认密码
        if (StringUtils.isBlank (newAdminBO.getImg64 ())){
            if (StringUtils.isBlank(newAdminBO.getPassword()) || StringUtils.isBlank(newAdminBO.getConfirmPassword())) {
                return CommonResponse.errorCustom(ResponseEnum.ADMIN_PASSWORD_NULL_ERROR);
            }
        }

        //密码不为空，则必须判断两次输入一致
        if (StringUtils.isNotBlank(newAdminBO.getPassword())) {
            if (!newAdminBO.getPassword().equalsIgnoreCase(newAdminBO.getConfirmPassword())) {
                return CommonResponse.errorCustom(ResponseEnum.ADMIN_PASSWORD_ERROR);
            }
        }

        //校验用户名唯一
        checkAdminExist(newAdminBO.getUsername());

        //保存到数据库
        adminUserService.createAdminUser(newAdminBO);
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse getAdminList(Integer page, Integer pageSize) {
        if (page == null) {
            page = COMMON_START_PAGE;
        }

        if (pageSize == null) {
            pageSize = COMMON_PAGE_SIZE;
        }

        PagedGridResult result = adminUserService.queryAdminList(page, pageSize);
        return CommonResponse.ok(result);
    }

    @Override
    public CommonResponse adminLogout(String adminId, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isBlank (adminId)){
            return CommonResponse.errorCustom (ResponseEnum.ADMIN_NOT_EXIT_ERROR);
        }
        redis.del (REDIS_ADMIN_TOKEN + ":" + adminId);
        setCookie(request, response, "atoken","",COOKIE_DEL,domain);
        setCookie(request, response, "aid", "",COOKIE_DEL,domain);
        setCookie(request, response, "aname", "",COOKIE_DEL,domain);
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse adminFaceLogin(AdminLoginBO adminLoginBO, HttpServletRequest request, HttpServletResponse response)throws Exception {

        //判断用户名和人脸信息不能为空
        if(StringUtils.isBlank (adminLoginBO.getUsername ())){
            return CommonResponse.errorCustom (ResponseEnum.ADMIN_NAME_NULL_ERROR);
        }
        String tempFace64 = adminLoginBO.getImg64();
        System.out.println (tempFace64 );
//        logger.info (tempFace64);
        if (StringUtils.isBlank (tempFace64)){
            return CommonResponse.errorCustom (ResponseEnum.ADMIN_FACE_NULL_ERROR);
        }

        // 1. 从数据库中查询出faceId
        AdminUser admin = adminUserService.queryAdminByUsername(adminLoginBO.getUsername());
        String adminFaceId = admin.getFaceId();
        if (StringUtils.isBlank(adminFaceId)) {
            return CommonResponse.errorCustom(ResponseEnum.ADMIN_FACE_LOGIN_ERROR);
        }

        // 2. 请求文件服务，获得人脸数据的base64数据
        String fileServerUrlExecute = "http://"+host+":8004/fs/readFace64InGridFS?faceId=" + adminFaceId;
        ResponseEntity<CommonResponse> entity = restTemplate.getForEntity (fileServerUrlExecute, CommonResponse.class);
        CommonResponse entityBody = entity.getBody ( );
        String base64DB = (String) entityBody.getData ();
//        System.out.println (base64DB );
        logger.info (base64DB);

        // 3. 调用阿里ai进行人脸对比识别，判断可信度，从而实现人脸登录
        boolean result = faceUtils.FaceVerify (base64DB,tempFace64,0.65);
        if (!result){
            CommonResponse.errorCustom (ResponseEnum.ADMIN_FACE_LOGIN_ERROR);
        }
        doLoginSettings(admin, request, response);
        return CommonResponse.ok ();
    }
}
