package com_apibancaria.dtos;

import java.time.LocalDate;

public record AccountDto(String firstName, String lastName, String nickName,
                         LocalDate dob, Integer age, Boolean isMale, String document,
                         String phone, String email, AddressDto address) {

    public AccountDto(String firstName, String lastName, String nickName, LocalDate dob, Integer age, Boolean isMale, String document, String phone, String email, AddressDto address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickName = nickName;
        this.dob = dob;
        this.age = age;
        this.isMale = isMale;
        this.document = document;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
}