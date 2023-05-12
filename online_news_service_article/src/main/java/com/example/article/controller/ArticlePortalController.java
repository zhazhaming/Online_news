package com.example.article.controller;

import com.example.CommonResponse.CommonResponse;
import com.example.api.BaseController;
import com.example.api.controller.article.ArticlePortalControllerApi;
import com.example.article.service.ArticlePortalService;
import com.example.pojo.Article;
import com.example.pojo.vo.AppUserVO;
import com.example.pojo.vo.IndexArticleVO;
import com.example.utilis.JsonUtils;
import com.example.utilis.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
public class ArticlePortalController extends BaseController implements ArticlePortalControllerApi {
    final static Logger logger = LoggerFactory.getLogger(ArticlePortalController.class);

    @Autowired
    private ArticlePortalService articlePortalService;

    @Autowired
    private RestTemplate restTemplate;

    @Value ("${website.domain-name}")
    private String domain;

    @Override
    public CommonResponse list(String keyword, Integer category, Integer page, Integer pageSize) {
        if (page==null){
            page = COMMON_START_PAGE;
        }
        if (pageSize==null){
            pageSize= COMMON_PAGE_SIZE;
        }
        PagedGridResult gridResult
                = articlePortalService.queryIndexArticleList(keyword,
                category,
                page,
                pageSize);

        PagedGridResult gridResultChange = rebuildArticleGrid (gridResult);
        return CommonResponse.ok (gridResultChange);
    }


    @Override
    public CommonResponse hotList() {
        return CommonResponse.ok (articlePortalService.queryHotList());
    }

    @Override
    public CommonResponse queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        if (page==null){
            page = COMMON_START_PAGE;
        }
        if (pageSize==null){
            pageSize= COMMON_PAGE_SIZE;
        }
        PagedGridResult pagedGridResult = articlePortalService
                .queryArticleListOfWriter (writerId, page, pageSize);
        //重构
        PagedGridResult pagedGridResultResult = rebuildArticleGrid (pagedGridResult);
        return CommonResponse.ok (pagedGridResultResult);
    }

    @Override
    public CommonResponse queryGoodArticleListOfWriter(String writerId) {
        PagedGridResult pagedGridResult = articlePortalService.queryGoodArticleListOfWriter (writerId);
        return CommonResponse.ok (pagedGridResult);
    }

    @Override
    public CommonResponse detail(String articleId) {
        return null;
    }

    @Override
    public Integer readCounts(String articleId) {
        return null;
    }

    @Override
    public CommonResponse readArticle(String articleId, HttpServletRequest request) {
        return null;
    }

    private PagedGridResult rebuildArticleGrid(PagedGridResult gridResult) {

        List<Article> list = (List<Article>)gridResult.getRows();

        // 1. 构建发布者id列表
        Set<String> idSet = new HashSet<>();
        List<String> idList = new ArrayList<>();
        for (Article a : list) {
            // 1.1 构建发布者的set
            idSet.add(a.getPublishUserId());
            // 1.2 构建文章id的list
//            idList.add(REDIS_ARTICLE_READ_COUNTS + ":" + a.getId());------------
        }
        // 发起redis的mget批量查询api，获得对应的值
//        List<String> readCountsRedisList = redis.mget(idList); -----------

        List<AppUserVO> publisherList = getPublisherList(idSet);


        // 3. 拼接两个list，重组文章列表
        List<IndexArticleVO> indexArticleList = new ArrayList<>();
        for (int i = 0 ; i < list.size() ; i ++) {
            IndexArticleVO indexArticleVO = new IndexArticleVO();
            Article a = list.get(i);
            BeanUtils.copyProperties(a, indexArticleVO);

            // 3.1 从publisherList中获得发布者的基本信息
            AppUserVO publisher  = getUserIfPublisher(a.getPublishUserId(), publisherList);
            indexArticleVO.setPublisherVO(publisher);

            // 3.2 重新组装设置文章列表中的阅读量
//            String redisCountsStr = readCountsRedisList.get(i);
//            int readCounts = 0;
//            if (StringUtils.isNotBlank(redisCountsStr)) {
//                readCounts = Integer.valueOf(redisCountsStr);
//            }
//            indexArticleVO.setReadCounts(readCounts);
//
            indexArticleList.add(indexArticleVO);
        }


        gridResult.setRows(indexArticleList);
// END
        return gridResult;
    }

    private AppUserVO getUserIfPublisher(String publisherId,
                                         List<AppUserVO> publisherList) {
        for (AppUserVO user : publisherList) {
            if (user.getId().equalsIgnoreCase(publisherId)) {
                return user;
            }
        }
        return null;
    }

    // 发起远程调用，获得用户的基本信息(restTemplate)
    private List<AppUserVO> getPublisherList(Set idSet) {
        String userServerUrlExecute
                = "http://"+domain+":8003/user/queryByIds?userIds=" + JsonUtils.objectToJson(idSet);
        ResponseEntity<CommonResponse> responseEntity = restTemplate.getForEntity (userServerUrlExecute, CommonResponse.class);
        CommonResponse bodyResult = responseEntity.getBody ( );
        List<AppUserVO> publisherList = null;
        if (bodyResult.getStatus() == 200) {
            String userJson = JsonUtils.objectToJson(bodyResult.getData());
            publisherList = JsonUtils.jsonToList(userJson, AppUserVO.class);
        }
        return publisherList;
    }

}
