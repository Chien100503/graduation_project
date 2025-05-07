package com.petshop.petopia.implement;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class PayosImpl {
    public long calculateExpiredTime() {
        // Lấy thời gian hiện tại tính bằng giây
        long currentTimeInSeconds = Calendar.getInstance().getTimeInMillis() / 1000;
        // Thêm 30 phút (1800 giây)
        return (int) (currentTimeInSeconds + 1800);
    }
}
