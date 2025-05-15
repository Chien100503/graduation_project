package com.petshop.petopia.config;

import com.ngrok.Forwarder;
import com.ngrok.HttpBuilder;
import com.ngrok.Session;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
@RequiredArgsConstructor
public class NgrokConfig implements CommandLineRunner {
    @Value("${ngrok.authtoken}")
    private String authtoken;

    @Value("${ngrok.domain}")
    private String reservedDomain;

    @SneakyThrows
    @Override
    public void run(String... args) {
        try {
            Session session = Session.withAuthtoken(authtoken).connect();

            HttpBuilder httpBuilder = session.httpEndpoint()
                    .domain(reservedDomain);

            URL localUrl = new URL("http://localhost:8080");

            Forwarder.Endpoint forwarder = session.forwardHttp(httpBuilder, localUrl);  // Cổng 8080

            System.out.println("Ngrok đang forward đến: " + forwarder.getUrl());

            new Thread(() -> {
                try {
                    forwarder.join();
                } catch (IOException e) {
                    System.err.println("❌ Ngrok forwarder lỗi: " + e.getMessage());
                }
            }).start();
        } catch (IOException e) {
            System.err.println("Không thể tạo ngrok session: " + e.getMessage());
        }
    }
}
