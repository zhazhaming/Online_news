package com.example.article.service.Impl;

import com.example.CommonException.CommonException;
import com.example.CommonResponse.ResponseEnum;
import com.example.api.service.BaseService;
import com.example.article.mapper.ArticleMapper;
import com.example.article.mapper.ArticleMapperCustom;
import com.example.article.service.ArticleService;
import com.example.enums.ArticleAppointType;
import com.example.enums.ArticleReviewLevel;
import com.example.enums.ArticleReviewStatus;
import com.example.enums.YesOrNo;
import com.example.pojo.Article;
import com.example.pojo.Category;
import com.example.pojo.bo.NewArticleBO;
import com.example.utilis.PagedGridResult;
import com.example.utilis.Sid;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl extends BaseService implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleMapperCustom articleMapperCustom;

    @Autowired
    private Sid sid;

    @Transactional
    @Override
    public void createArticle(NewArticleBO newArticleBO, Category category) {
        String articleId =sid.nextShort ();
        Article article = new Article ();
        BeanUtils.copyProperties (newArticleBO,article);
        article.setId (articleId);
        article.setCategoryId (category.getId ());
        article.setArticleStatus(ArticleReviewStatus.REVIEWING.type);
        article.setCommentCounts(0);
        article.setReadCounts(0);

        article.setIsDelete(YesOrNo.NO.type);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        //设置发布时间
        if (article.getIsAppoint() == ArticleAppointType.TIMING.type) {
            article.setPublishTime(newArticleBO.getPublishTime());
        } else if (article.getIsAppoint() == ArticleAppointType.IMMEDIATELY.type) {
            article.setPublishTime(new Date());
        }

        int res = articleMapper.insert(article);
        if (res != 1) {
            CommonException.display(ResponseEnum.ARTICLE_CREATE_ERROR);
        }

        //第三方阿里云内容安全检测需要企业认证开通不了，直接写死。
        String textResult = ArticleReviewLevel.REVIEW.type;

        if (textResult.equals (ArticleReviewLevel.PASS.type)){
            this.updateArticleStatus (articleId,ArticleReviewStatus.SUCCESS.type);
        }else if (textResult.equals (ArticleReviewLevel.BLOCK.type)){
            this.updateArticleStatus (articleId,ArticleReviewStatus.FAILED.type);
        }else if (textResult.equals (ArticleReviewLevel.REVIEW.type)){
            this.updateArticleStatus (articleId,ArticleReviewStatus.WAITING_MANUAL.type);
        }

    }

    @Transactional
    @Override
    public void updateAppointToPublish() {
        articleMapperCustom.updateAppointToPublish ();
    }

    @Override
    public void updateArticleToPublish(String articleId) {

    }


    @Override
    public PagedGridResult queryMyArticleList(String userId, String keyword, Integer status, Date startDate, Date endDate, Integer page, Integer pageSize) {

        Example example = new Example (Article.class);
        example.orderBy ("createTime").desc ();
        Example.Criteria criteria = example.createCriteria ();

        criteria.andEqualTo ("publishUserId",userId);

        if (StringUtils.isNotBlank (keyword)){
            criteria.andLike ("title","%"+keyword+"%");
        }
        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }
        if (status != null&&status == 12){
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        if (startDate!=null){
            criteria.andGreaterThan ("createTime",startDate);
        }
        if (endDate!=null){
            criteria.andLessThan ("updateTime",endDate);
        }
        PageHelper.startPage (page,pageSize);
        List<Article> articleList = articleMapper.selectByExample (example);
        return setterPagedGrid (articleList,page);
    }

    @Transactional
    @Override
    public void updateArticleStatus(String articleId, Integer pendingStatus) {
        Example example = new Example(Article.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo ("id",articleId);

        Article article = new Article ();
        article.setArticleStatus (pendingStatus);
        int res = articleMapper.updateByExampleSelective (article, example);
        if (res!=1){
            CommonException.display (ResponseEnum.ARTICLE_REVIEW_ERROR);
        }
    }

    @Override
    public void updateArticleToGridFS(String articleId, String articleMongoId) {

    }

    @Override
    public PagedGridResult queryAllArticleListAdmin(Integer status, Integer page, Integer pageSize) {
        Example example = new Example (Article.class);
        example.orderBy ("createTime").desc ();
        Example.Criteria criteria = example.createCriteria ();

        if (ArticleReviewStatus.isArticleStatusValid(status)) {
            criteria.andEqualTo("articleStatus", status);
        }
        if (status != null&&status == 12){
            criteria.andEqualTo("articleStatus", ArticleReviewStatus.REVIEWING.type)
                    .orEqualTo("articleStatus", ArticleReviewStatus.WAITING_MANUAL.type);
        }
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);

        PageHelper.startPage (page,pageSize);
        List<Article> articleList = articleMapper.selectByExample (example);
        return setterPagedGrid (articleList,page);
    }

    @Transactional
    @Override
    public void deleteArticle(String userId, String articleId) {
        Example example = makeExampleCriteria (userId,articleId);
        Article article = new Article ();
        article.setIsDelete (YesOrNo.YES.type);
        int result = articleMapper.updateByExampleSelective (article, example);
        if (result!=1){
            CommonException.display (ResponseEnum.ARTICLE_DELETE_ERROR);
        }
    }

    @Transactional
    @Override
    public void withdrawArticle(String userId, String articleId) {
        Example example = makeExampleCriteria (userId,articleId);
        Article article = new Article ();
        article.setArticleStatus (ArticleReviewStatus.WITHDRAW.type);
        int result = articleMapper.updateByExampleSelective (article, example);
        if (result!=1){
            CommonException.display (ResponseEnum.ARTICLE_DELETE_ERROR);
        }
    }

    private Example makeExampleCriteria(String userId, String articleId) {
        Example articleExample = new Example(Article.class);
        Example.Criteria criteria = articleExample.createCriteria();
        criteria.andEqualTo("publishUserId", userId);
        criteria.andEqualTo("id", articleId);
        return articleExample;
    }
}
