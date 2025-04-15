package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐管理")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    @GetMapping("/page")
    @ApiOperation("分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询");
        PageResult pageResult =  setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增菜品");
        setmealService.save(setmealDTO);
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询套餐")
    public Result<SetmealVO> get(@PathVariable Long id) {
        log.info("根据Id查找");
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }
    @PostMapping("/status/{status}")
    @ApiOperation("修改套餐状态")
    public Result updateStatus(@PathVariable Integer status, Long id) {
        log.info("修改套餐状态");
        setmealService.updateStatus(status,id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐");
        setmealService.update(setmealDTO);
        return Result.success();
    }
}
