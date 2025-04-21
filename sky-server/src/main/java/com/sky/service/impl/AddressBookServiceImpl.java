package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public List<AddressBook> getAllAddressBook() {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> list =  addressBookMapper.getAllAddressBook(userId);
        return list;
    }

    @Override
    public void addAddressBook(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBookMapper.add(addressBook);

    }

    @Override
    public AddressBook getByUserIdDefault() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getByUserIdDefault(userId);
        return addressBook;
    }

    @Override
    public void updateAddressBookDefault(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> allAddressBook = addressBookMapper.getAllAddressBook(userId);
        for (AddressBook addressBook1 : allAddressBook) {addressBook1.setIsDefault(0);addressBookMapper.updateAddressBook(addressBook1);}
        addressBook.setIsDefault(1);
        addressBookMapper.updateAddressBook(addressBook);
    }

    @Override
    public void updateAddressBook(AddressBook addressBook) {
        addressBookMapper.updateAddressBook(addressBook);
    }

    @Override
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        return addressBook;
    }

    @Override
    public void deleteAddressBookById(Integer id) {
        addressBookMapper.deleteAddressBookById(id);
    }


}
