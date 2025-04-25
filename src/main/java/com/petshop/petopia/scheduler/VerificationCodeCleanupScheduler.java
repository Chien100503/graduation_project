package com.petshop.petopia.scheduler;

import com.petshop.petopia.service.VerificationCodeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationCodeCleanupScheduler {

    private final VerificationCodeService cleanupService;

    @Scheduled(fixedRate = 60000)
    public void cleanUpExpiredCodes() {
        cleanupService.cleanUp(); // gọi service để xóa
    }

    @PostConstruct
    public void runOnceAtStartup() {
        cleanupService.cleanUp(); // gọi service lúc khởi động
    }
}
