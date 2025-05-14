package com.petshop.petopia.implement;

import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class PayosImpl {
    public long calculateExpiredTime() {
        long currentTimeInSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        return (int) (currentTimeInSeconds + 1800);
    }
}
