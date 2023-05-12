package com.example.article.controller;

import com.example.CommonException.CommonException;
import com.example.CommonResponse.CommonResponse;
import com.example.CommonResponse.ResponseEnum;
import com.example.api.BaseController;
import com.example.api.controller.article.ArticleControllerApi;
import com.example.article.service.ArticleService;
import com.example.enums.ArticleCoverType;
import com.example.enums.ArticleReviewStatus;
import com.example.enums.YesOrNo;
import com.example.pojo.Category;
import com.example.pojo.bo.NewArticleBO;
import com.example.utilis.JsonUtils;
import com.example.utilis.PagedGridResult;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
public class ArticleController extends BaseController implements ArticleControllerApi {

    final static Logger logger = LoggerFactory.getLogger(ArticleController.class);
    @Autowired
    private ArticleService articleService;

    @Override
    public CommonResponse createArticle(NewArticleBO newArticleBO, BindingResult result) {
        if (result.hasErrors ()){
            Map<String, String> errorMap = getErrors(result);
            return CommonResponse.errorMap(errorMap);
        }
        //判断文章封面类型，单图必填，纯文字则设置为空
        if (newArticleBO.getArticleType () == ArticleCoverType.ONE_IMAGE.type){
            if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                return CommonResponse.errorCustom(ResponseEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
            }
        } else if (newArticleBO.getArticleType() == ArticleCoverType.WORDS.type) {
            newArticleBO.setArticleCover("");
        }

        //判断分类id是否存在
        String allCatJson = redis.get (REDIS_ALL_CATEGORY);
        Category temp = null;
        if (StringUtils.isBlank (allCatJson)){
            CommonResponse.errorCustom (ResponseEnum.SYSTEM_OPERATION_ERROR);
        }else {
            List<Category> categoryList = JsonUtils.jsonToList (allCatJson,Category.class);

            for (Category category :categoryList) {
                if (newArticleBO.getCategoryId () == category.getId ()){
                    temp = category;
                    break;
                }
            }
            if (temp == null){
                CommonResponse.errorCustom (ResponseEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
            }
        }
//        System.out.println (newArticleBO.toString () );
        articleService.createArticle (newArticleBO,temp);
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse queryMyList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {

        if (StringUtils.isBlank (userId)){
            CommonException.display (ResponseEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }
        PagedGridResult articleList = articleService.queryMyArticleList (userId, keyword, status, startDate, endDate, page, pageSize);
        return CommonResponse.ok (articleList);
    }

    @Override
    public CommonResponse queryAllList(Integer status, Integer page, Integer pageSize) {
        PagedGridResult AdminarticleList = articleService.queryAllArticleListAdmin (status,page,pageSize);
        return CommonResponse.ok (AdminarticleList);
    }

    @Override
    public CommonResponse doReview(String articleId, Integer passOrNot) {
        Integer pendingStatus;
        if (passOrNot== YesOrNo.YES.type){
            pendingStatus = ArticleReviewStatus.SUCCESS.type;
        }else if (passOrNot == YesOrNo.NO.type){
            pendingStatus = ArticleReviewStatus.FAILED.type;
        }else {
            return CommonResponse.errorCustom (ResponseEnum.ARTICLE_REVIEW_ERROR);
        }
        articleService.updateArticleStatus (articleId,pendingStatus);
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse delete(String userId, String articleId) {
        if (!userId.equalsIgnoreCase (userId)){
            return CommonResponse.errorCustom (ResponseEnum.USER_STATUS_ERROR);
        }
        articleService.deleteArticle (userId,articleId);
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse withdraw(String userId, String articleId) {
        if (!userId.equalsIgnoreCase (userId)){
            return CommonResponse.errorCustom (ResponseEnum.USER_STATUS_ERROR);
        }
        articleService.withdrawArticle (userId,articleId);
        return CommonResponse.ok ();
    }
}
