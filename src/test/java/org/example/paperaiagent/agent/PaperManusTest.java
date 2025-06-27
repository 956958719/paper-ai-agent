package org.example.paperaiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class PaperManusTest {

    @Resource
    private PaperManus paperManus;

    @Test
    public void run(){
        String userPrompt = """
                帮我搜索detr这篇论文，下载下来，然后解析pdf，最后告诉我它的实验结果是如何？
                """;
        String answer = paperManus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }

}