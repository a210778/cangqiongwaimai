package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "分类管理")
@Slf4j
@RestController("adminCategoryController")
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 修改分类
     * @param category
     * @return
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改{}", categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> getAllCategory(CategoryPageQueryDTO categoryPageQueryDTO) {
    log.info("分页查询{}", categoryPageQueryDTO);
    PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
    return Result.success(pageResult);
    }

    /**
     * 分类状态
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("分类状态")
    public Result updateCategoryStatus(@PathVariable Integer status,  long id) {
        log.info("修改分类状态{}，{}", status,id);
        categoryService.updateCategoryStatus(status,id);
        return Result.success();
    }
    @PostMapping
    @ApiOperation("新增分类")
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分裂{}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除分类")
    public Result deleteCategory(long id) {
        log.info("删除分类");
        categoryService.deleteCategory(id);
        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("根据类型查询")
    public Result<List<Category>> listCategory(Integer type) {
        log.info("根据类型查询");
       List<Category> list =  categoryService.list(type);
        return Result.success(list);
    }

}
