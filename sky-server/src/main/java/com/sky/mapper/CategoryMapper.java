package com.sky.mapper;
import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);

    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) VALUES " +
            "(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void addCategory(Category category);
    @Delete("delete from category where id = #{id}")
    void deleteCategory(long id);
}
