package com.intelligent.bot.base.exception;



@SuppressWarnings("all")
public class E extends RuntimeException {

    public E(String message) {
        super(message);
    }
    public void throwMessage() {
        throw this;
    }

}
