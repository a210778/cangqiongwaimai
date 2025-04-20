package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    List<AddressBook> getAllAddressBook();

    void addAddressBook(AddressBook addressBook);


    AddressBook getByUserIdDefault();

    void updateAddressBookDefault(AddressBook addressBook);


    void updateAddressBook(AddressBook addressBook);

    AddressBook getById(int id);

    void deleteAddressBookById(Integer id);
}
