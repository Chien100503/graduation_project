package com.petshop.petopia;

import com.ngrok.Session;
import com.ngrok.Listener;
import com.ngrok.Forwarder;
import com.ngrok.HttpBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.net.URL;

@SpringBootApplication
public class PetopiaApplication {

	@Value("${ngrok.authtoken}")
	private String authtoken;

	@Value("${ngrok.domain}")
	private String reservedDomain;

	public static void main(String[] args) {
		SpringApplication.run(PetopiaApplication.class, args);
	}

	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		try {
			// Tạo session ngrok với auth token
			Session session = Session.withAuthtoken(authtoken).connect();

			// Cấu hình HttpBuilder với domain tĩnh
			HttpBuilder httpBuilder = session.httpEndpoint()
					.domain(reservedDomain);  // Chỉ định domain tĩnh

			// Tạo HTTP Forwarder với cổng đã chỉ định
			Forwarder.Endpoint forwarder = session.forwardHttp(httpBuilder, new URL("http://localhost:8080"));  // Cổng 8080

			// In ra URL forwarder
			System.out.println("Ngrok đang forward đến: " + forwarder.getUrl());

			// Chờ đến khi forwarder dừng
			forwarder.join();
		} catch (IOException e) {
			System.err.println("Không thể tạo ngrok session: " + e.getMessage());
		}
	}
}
