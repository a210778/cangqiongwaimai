package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> getSetmealDishIds(List<Long> dishIds);

    void insert(List<SetmealDish> list);
    @Select("select *from setmeal_dish where setmeal_id =#{setmealId}")
    List<SetmealDish> getSetmealDishId(Long setmealId);
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void delete(Long setmealId);
    @Select("select *from setmeal where id= #{id}")
    Setmeal getById(Long id);

    void deleteByIds(@Param("setmealIds") List<Long> setmealIds);
}
