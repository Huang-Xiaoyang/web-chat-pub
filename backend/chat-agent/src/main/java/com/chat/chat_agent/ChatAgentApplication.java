package com.chat.chat_agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ChatAgentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatAgentApplication.class, args);
	}

}
