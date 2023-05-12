package com.example.article.mapper;

import com.example.my.mapper.MyMapper;
import com.example.pojo.Article;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper extends MyMapper<Article> {
}