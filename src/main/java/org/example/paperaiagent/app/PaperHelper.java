package org.example.paperaiagent.app;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.paperaiagent.advisor.MyLoggerAdvisor;
import org.example.paperaiagent.chatmemory.FileBasedChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class PaperHelper {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你需要扮演一个撰写计算机视觉领域（动物姿态估计方向）的研究生学位论文的专家。" +
            "开场向用户表明身份,告知用户可以向你提问论文相关的问题。" +
            "若是之前已经说过开场白了，则不再需要告知用户自己的身份。" +
            "请注意，论文检索工具只支持英文搜索，请把搜索关键词转化为英文再使用该工具。" +
            "若是检索到论文，则只有论文标题无需翻译，其它的内容、关键字等等需要翻译成中文。" +
            "在使用检索工具之后，注意要标注该论文的id，如arxiv_id或者paper_id。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public PaperHelper(ChatModel dashscopeChatModel){
        // 初始化基于文件的ChatMemory
        String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);

        // 初始化chatClient，用于与LLM进行对话，并设置系统提示词以及设置两个顾问：基于文件的对话记忆、日志输出
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
//                        ,new MyLoggerAdvisor()
                        , new SimpleLoggerAdvisor()
                )
                .build();

    }

    /**
     * AI 基础对话（支持上下文记忆）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId) // 对话id
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10)) // 历史对话记忆10条
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    @Resource
    private Advisor myRagCloudAdvisor;

    @Resource
    VectorStore pgVectorVectorStore;

    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 应用知识库问答
                .advisors(myRagCloudAdvisor)
                // 应用pgVectorStore知识库问答
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    // MCP 服务
    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    // AI 调用本地工具能力
    @Resource
    private ToolCallback[] allTools;

    public String doChatWithMcp(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .tools(toolCallbackProvider)
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
}
