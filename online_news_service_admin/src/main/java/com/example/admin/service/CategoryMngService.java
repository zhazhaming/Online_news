package com.example.admin.service;

import com.example.pojo.Category;
import com.example.pojo.bo.SaveCategoryBO;
import io.swagger.models.auth.In;

import javax.swing.*;
import java.util.List;

public interface CategoryMngService {
    //新增分类
    public void createCategory(Category category);

    //修改文章分类列表
    public void modifyCategory(Category category);

    //查询分类名是否已经存在
    public boolean queryCatIsExist(String catName, String oldCatName);

    //获得文章分类列表
    public List<Category> queryCategoryList();

    //删除文章分类列表
    public  void deleteCategory(Integer categoryId);
}
