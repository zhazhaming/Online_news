package com.example.admin.controller;

import com.example.CommonResponse.CommonResponse;
import com.example.CommonResponse.ResponseEnum;
import com.example.admin.service.CategoryMngService;
import com.example.api.BaseController;
import com.example.api.controller.admin.CategoryMngControllerApi;
import com.example.pojo.Category;
import com.example.pojo.bo.SaveCategoryBO;
import com.example.utilis.JsonUtils;
import com.example.utilis.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.api.service.BaseService.REDIS_ALL_CATEGORY;

@RestController
public class CategoryMngController extends BaseController implements CategoryMngControllerApi {

    final static Logger logger = LoggerFactory.getLogger(CategoryMngController.class);

    @Autowired
    CategoryMngService categoryMngService;

    @Autowired
    RedisOperator redis;

    @Override
    public CommonResponse saveOrUpdateCategory(SaveCategoryBO newCategoryBO) {

        Category category = new Category();
        BeanUtils.copyProperties (newCategoryBO,category);
        // id为空新增，不为空修改
        if(newCategoryBO.getId ()==null){
            // 查询新增的分类名称不能重复存在
            boolean isExist = categoryMngService.queryCatIsExist (newCategoryBO.getName ( ), null);
            if (!isExist){
                categoryMngService.createCategory (category);
            }else {
                return CommonResponse.errorCustom (ResponseEnum.CATEGORY_EXIST_ERROR);
            }
        }else {
            // 查询修改的分类名称不能重复存在
            boolean isExist = categoryMngService.queryCatIsExist (newCategoryBO.getName ( ), newCategoryBO.getOldName ());
            if (!isExist){
                categoryMngService.modifyCategory (category);
            }else {
                return CommonResponse.errorCustom (ResponseEnum.CATEGORY_EXIST_ERROR);
            }
        }
        categoryMngService.createCategory (category);
        return CommonResponse.ok ();
    }

    @Override
    public CommonResponse getCatList() {
        List<Category> categories = categoryMngService.queryCategoryList ( );
        return CommonResponse.ok (categories);
    }

    @Override
    public CommonResponse getCats() {
        String allCatJson = redis.get(REDIS_ALL_CATEGORY);
        List<Category> categoryList = null;
        if (StringUtils.isBlank (allCatJson)){
            categoryList = categoryMngService.queryCategoryList ();
            redis.set (REDIS_ALL_CATEGORY,JsonUtils.objectToJson (categoryList));
        }else{
            categoryList = JsonUtils.jsonToList (allCatJson, Category.class);
        }
        return CommonResponse.ok (categoryList);
    }

    @Override
    public CommonResponse deleteCategory(Integer categoryId) {
        categoryMngService.deleteCategory (categoryId);
        return CommonResponse.ok ();
    }
}
