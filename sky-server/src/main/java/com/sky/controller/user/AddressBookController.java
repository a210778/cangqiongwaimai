package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "用户地址薄接口")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    @ApiOperation("新增地址")
    public Result<AddressBook> addAddressBook(@RequestBody AddressBook addressBook) {
    log.info("新增地址");
        addressBookService.addAddressBook(addressBook);
        return Result.success(addressBook);
    }

    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> getAddressBookList() {
        log.info("查询当前登录用户的所有地址信息");
        List<AddressBook> addressBooks = addressBookService.getAllAddressBook();
        return Result.success(addressBooks);
    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> getDefaultAddressBook() {
        log.info("查询默认地址");
       AddressBook addressBook = addressBookService.getByUserIdDefault();
        return Result.success(addressBook);
    }

    @PutMapping("default")
    @ApiOperation("设置默认地址")
    public Result updateDefaultAddressBook(@RequestBody AddressBook addressBook) {
        log.info("设置默认地址");
        addressBookService.updateAddressBookDefault(addressBook);
        return Result.success();
    }
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result updateAddressBookById(@RequestBody AddressBook addressBook) {
        log.info("根据id修改地址");
        addressBookService.updateAddressBook(addressBook);
        return Result.success();

    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getAddressBookById(@PathVariable Integer id) {
        log.info("根据id查询地址");
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteAddressBookById(Integer id) {
        log.info("根据id删除地址");
        addressBookService.deleteAddressBookById(id);
        return Result.success();
    }
}
