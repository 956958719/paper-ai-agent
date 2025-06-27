package org.example.paperaiagent.chatmemory;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBasedChatMemoryTest {

    @Test
    void add() {
        String conversationId = "114514";
        Message message = new UserMessage("测试kryo持久化功能");
        List<Message> messages = new ArrayList<>();
        messages.add(message);
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        FileBasedChatMemory fileBasedChatMemory = new FileBasedChatMemory(fileDir);
        fileBasedChatMemory.add(conversationId, messages);
    }

    @Test
    void get() {
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        FileBasedChatMemory fileBasedChatMemory = new FileBasedChatMemory(fileDir);
        String conversationId = "4";
        List<Message> messages = fileBasedChatMemory.get(conversationId, 10);
        Assertions.assertNotNull(messages);
    }

    @Test
    void clear() {
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        FileBasedChatMemory fileBasedChatMemory = new FileBasedChatMemory(fileDir);
        String conversationId = "114514";
        fileBasedChatMemory.clear(conversationId);
    }
}