package ru.bergstart.bergstart;

public class PhoneInvalidException extends RuntimeException {

    String phone;

    public PhoneInvalidException(String phone) {
        this.phone = phone;
    }
}
