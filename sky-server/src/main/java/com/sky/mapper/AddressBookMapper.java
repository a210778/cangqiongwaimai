package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    @Select("select *from address_book where user_id=#{userId}")
    List<AddressBook> getAllAddressBook(Long userId);
    void add(AddressBook addressBook);
    @Select("select *from address_book where user_id=#{userId} and is_default=1")
    AddressBook getByUserIdDefault(Long userId);

    void updateAddressBook(AddressBook addressBook);
    @Select("select *from address_book where id =#{id}")
    AddressBook getById(int id);
    @Delete("delete from address_book where id = #{id}")
    void deleteAddressBookById(Integer id);
}
