package com.ISP392.demo;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class App {
	@Value("djyw3ytjd")
	private String cloudName;

	@Value("187816958334856")
	private String apiKey;

	@Value("uN1GmwZ112s9yRrjhivOm2r8KZU")
	private String apiSecret;

	@Bean
	public Cloudinary cloudinaryConfig() {
		Cloudinary cloudinary = null;
		Map<String, String> config = new HashMap<String, String>();
		config.put("cloud_name", cloudName);
		config.put("api_key", apiKey);
		config.put("api_secret", apiSecret);
		cloudinary = new Cloudinary(config);
		return cloudinary;
	}

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
