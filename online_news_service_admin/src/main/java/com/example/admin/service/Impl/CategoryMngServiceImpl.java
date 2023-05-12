package com.example.admin.service.Impl;

import com.example.CommonException.CommonException;
import com.example.CommonResponse.ResponseEnum;
import com.example.admin.mapper.CategoryMapper;
import com.example.admin.service.CategoryMngService;
import com.example.api.service.BaseService;
import com.example.pojo.Category;
import com.example.pojo.bo.SaveCategoryBO;
import com.example.utilis.RedisOperator;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@Service
public class CategoryMngServiceImpl extends BaseService implements CategoryMngService {

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    RedisOperator redis;

    @Transactional
    @Override
    public void createCategory(Category category) {
        int result = categoryMapper.insertSelective (category);
        if (result!=1){
            CommonException.display (ResponseEnum.SYSTEM_OPERATION_ERROR);
        }
        redis.del (REDIS_ALL_CATEGORY);
    }

    @Transactional
    @Override
    public void modifyCategory(Category category) {
        int result = categoryMapper.updateByPrimaryKeySelective (category);
        if (result!=1){
            CommonException.display (ResponseEnum.SYSTEM_OPERATION_ERROR);
        }
        redis.del (REDIS_ALL_CATEGORY);
    }

    @Override
    public boolean queryCatIsExist(String catName, String oldCatName) {
        Example example = new Example(Category.class);
        Example.Criteria catCriteria = example.createCriteria();
        catCriteria.andEqualTo("name", catName);
        if (StringUtils.isNotBlank(oldCatName)) {
            catCriteria.andNotEqualTo("name", oldCatName);
        }

        List<Category> catList = categoryMapper.selectByExample(example);

        boolean isExist = false;
        if (catList != null && !catList.isEmpty() && catList.size() > 0) {
            isExist = true;
        }

        return isExist;
    }

    @Override
    public List<Category> queryCategoryList() {
        return categoryMapper.selectAll ( );
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        Example example = new Example(Category.class);
        Example.Criteria catCriteria = example.createCriteria();
        catCriteria.andEqualTo("id", categoryId);

        if (StringUtils.isBlank (categoryId.toString ())){
            CommonException.display (ResponseEnum.SYSTEM_OPERATION_ERROR);
        }
        int result = categoryMapper.deleteByExample (example);
        if (result!=1){
            CommonException.display (ResponseEnum.CATEGORY_DELETE_ERROR);
        }
        redis.del (REDIS_ALL_CATEGORY);
    }
}
