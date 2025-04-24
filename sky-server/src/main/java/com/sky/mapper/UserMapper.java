package com.sky.mapper;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select *from user where openid = #{openid}")
    User wechatLogin(String openid);

    void insert(User build);
    @Select("select *from user where id =#{userId}")
    User getById(Long userId);
    @Select("SELECT count(*) from user where create_time>#{startOfDay} and create_time<#{endOfDay}")
    Long getByTime(LocalDateTime startOfDay,LocalDateTime endOfDay);
    @Select("select count(*) from user where create_time<#{endOfDay}")
    Long getByTimeTotal(LocalDateTime endOfDay);

    Integer countByMap(Map map);
}
