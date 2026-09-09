package org.example.smart_hub.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("Bạn là trợ lý AI thông minh của Trung tâm Vận hành Logistics RikkeiExpress (SmartHub). Luôn phản hồi lịch sự, chính xác và chuyên nghiệp.")
                .build();
    }
}
