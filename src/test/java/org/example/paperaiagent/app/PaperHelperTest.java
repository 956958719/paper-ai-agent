package org.example.paperaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaperHelperTest {
    @Resource
    private PaperHelper paperHelper;

    @Test
    void doChat() {
        String message = "hrnet是什么？";
        String chatId = "1";
        String s = paperHelper.doChat(message, chatId);
        Assertions.assertNotNull(s);
    }

    @Test
    void doChatWithRag() {
        String message = "higher hrnet是什么？";
        String chatId = "20";
        String s = paperHelper.doChatWithRag(message, chatId);
        Assertions.assertNotNull(s);
        message = "在哪些数据集上跑过？真实的实验结果是多少？";
        s = paperHelper.doChatWithRag(message, chatId);
        Assertions.assertNotNull(s);
    }

    @Test
    void doChatWithMcp() {
//        String message = "请搜索“多视图聚类、关于切比雪夫图过滤器的”相关的论文，可能涉及到复杂查询，并且下载其中1篇论文，同时打开这个pdf来读取里面的内容，并告诉我里面的大致内容、实验数据集和实验结果数据。";
        String message = "请搜索“关于图过滤器的”相关的论文，可能涉及到复杂查询，并且下载其中1篇论文，同时打开这个pdf来读取里面的内容，并告诉我里面的大致内容、实验数据集和实验结果数据。最后告诉我你都调用了什么工具。";
        String chatId = "17";
        String s = paperHelper.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(s);
    }
}