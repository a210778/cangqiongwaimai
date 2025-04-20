package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    List<ShoppingCart> list(ShoppingCart shoppingCart);
    @Update("update shopping_cart set number=#{number} where id =#{id}")
    void updateNumberById(ShoppingCart shoppingCart);
    @Insert("insert into shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time,number) " +
            "values (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime},#{number})")
    void insert(ShoppingCart shoppingCart);
    @Select("select *from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> getById(Long userId);

    void sub(ShoppingCartDTO shoppingCartDTO);
    @Delete("delete from shopping_cart where user_id=#{userId}")
    void clean(Long userId);
}
