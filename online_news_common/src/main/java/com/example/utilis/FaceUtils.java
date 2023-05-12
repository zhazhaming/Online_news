package com.example.utilis;

import com.aliyun.facebody20191230.Client;
import com.example.utilis.extend.AliyunResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaceUtils {

    @Autowired
    private AliyunResource aliyunResource;

    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "facebody.cn-shanghai.aliyuncs.com";
        return new com.aliyun.facebody20191230.Client(config);
    }

    /**
     * 使用STS鉴权方式初始化账号Client，推荐此方式。本示例默认使用AK&SK方式。
     * @param accessKeyId
     * @param accessKeySecret
     * @param securityToken
     * @return Client
     * @throws Exception
     */
    public Client createClientWithSTS(String accessKeyId, String accessKeySecret, String securityToken) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret)
                // 必填，您的 Security Token
                .setSecurityToken(securityToken)
                // 必填，表明使用 STS 方式
                .setType("sts");
        // 访问的域名
        config.endpoint = "facebody.cn-shanghai.aliyuncs.com";
        return new com.aliyun.facebody20191230.Client(config);
    }

    public Boolean FaceVerify(String baseImageA,String baseImageB,Double target) throws Exception {
        // 工程代码泄露可能会导致AccessKey泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.facebody20191230.Client client = createClient(aliyunResource.getAccessKeyID (), aliyunResource.getAccessKeySecret ());
        com.aliyun.facebody20191230.models.CompareFaceRequest compareFaceRequest = new com.aliyun.facebody20191230.models.CompareFaceRequest()
                .setImageDataA (baseImageA)
                .setImageDataB (baseImageB);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.facebody20191230.models.CompareFaceResponse resp = client.compareFaceWithOptions(compareFaceRequest, runtime);
        Float confidence = resp.body.data.confidence;
//        com.aliyun.teaconsole.Client.log(com.aliyun.teautil.Common.toJSONString(resp));
        if (target>confidence){
            return true;
        }else {
            return false;
        }
    }

    public static void main(String[] args) {
        FaceUtils faceUtils = new FaceUtils ();
    }
}
