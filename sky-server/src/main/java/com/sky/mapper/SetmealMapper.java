package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(long id);

    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
    @AutoFill(value = OperationType.INSERT)
    void save(Setmeal setmeal);
    @Select("select *from setmeal where id = #{id}")
    Setmeal getById(Long id);
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);


    void deleteByIds(List<Long> ids);
}
