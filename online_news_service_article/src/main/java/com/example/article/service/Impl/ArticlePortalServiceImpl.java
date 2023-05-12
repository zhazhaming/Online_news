package com.example.article.service.Impl;

import com.example.CommonResponse.CommonResponse;
//import com.example.api.controller.article.ArticlePortalControllerApi;
import com.example.api.service.BaseService;
import com.example.article.mapper.ArticleMapper;
import com.example.article.service.ArticlePortalService;
import com.example.enums.ArticleReviewStatus;
import com.example.enums.YesOrNo;
import com.example.pojo.Article;
import com.example.pojo.vo.ArticleDetailVO;
import com.example.utilis.PagedGridResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.Area;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Service
public class ArticlePortalServiceImpl extends BaseService implements ArticlePortalService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PagedGridResult queryIndexArticleList(String keyword, Integer category, Integer page, Integer pageSize) {
        Example articleExample = new Example(Article.class);

        // 复用，获取满足硬性条件的criteria
        Example.Criteria criteria = setDefualArticleExample (articleExample);

        if (StringUtils.isNotBlank(keyword)) {
            criteria.andLike("title", "%" + keyword + "%");
        }
        if (category != null) {
            criteria.andEqualTo("categoryId", category);
        }

        PageHelper.startPage(page, pageSize);
        List<Article> list = articleMapper.selectByExample(articleExample);

        return setterPagedGrid(list, page);
    }

    @Override
    public List<Article> queryHotList() {
        Example articleExample = new Example(Article.class);

        // 复用，获取满足硬性条件的criteria
        Example.Criteria criteria = setDefualArticleExample (articleExample);
        PageHelper.startPage (1, 5);
        List<Article> articleList = articleMapper.selectByExample (articleExample);
        return articleList;
    }

    @Override
    public PagedGridResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        Example articleExample = new Example(Article.class);

        // 复用，获取满足硬性条件的criteria
        Example.Criteria criteria = setDefualArticleExample (articleExample);
        criteria.andEqualTo ("publishUserId",writerId);
        PageHelper.startPage (page,pageSize);
        List<Article> writerArticleList = articleMapper.selectByExample (articleExample);
        return setterPagedGrid (writerArticleList,page);
    }

    @Override
    public PagedGridResult queryGoodArticleListOfWriter(String writerId) {
        Example articleExample = new Example(Article.class);

        // 复用，获取满足硬性条件的criteria
        Example.Criteria criteria = setDefualArticleExample (articleExample);
        criteria.andEqualTo ("publishUserId",writerId);
        PageHelper.startPage (1,5);
        List<Article> writerArticleList = articleMapper.selectByExample (articleExample);
        return setterPagedGrid (writerArticleList,1);
    }

    @Override
    public ArticleDetailVO queryDetail(String articleId) {
        return null;
    }


    private Example.Criteria setDefualArticleExample(Example articleExample){
        articleExample.orderBy("publishTime").desc();
        Example.Criteria criteria = articleExample.createCriteria();

        /**
         * 查询首页文章的自带隐性查询条件：
         * isAppoint=即使发布，表示文章已经直接发布的，或者定时任务到点发布的
         * isDelete=未删除，表示文章只能够显示未删除
         * articleStatus=审核通过，表示只有文章经过机审/人工审核之后才能展示
         */
        criteria.andEqualTo("isAppoint", YesOrNo.NO.type);
        criteria.andEqualTo("isDelete", YesOrNo.NO.type);
        criteria.andEqualTo("articleStatus", ArticleReviewStatus.SUCCESS.type);
        return criteria;
    }
}
