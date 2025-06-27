package org.example.paperaiagent.agent;


import jakarta.annotation.Resource;
import org.example.paperaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

/**
 * 鱼皮的 AI 超级智能体（拥有自主规划能力，可以直接使用）
 */
@Component
public class PaperManus extends ToolCallAgent {

    public PaperManus(ToolCallback[] allTools, ToolCallbackProvider toolCallbackProvider, ChatModel dashscopeChatModel) {
        super(allTools, toolCallbackProvider);
        this.setName("PaperManus");
        String SYSTEM_PROMPT = """
                You are PaperManus, a computer paper writing assistant designed to help graduate students write their thesis.
                You have various tools at your disposal that you can call upon to efficiently complete complex requests.
                For the paper retrieval tool, inputs must be in English.
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools.
                For complex tasks, you can break down the problem and use different tools step by step to solve it.
                After using each tool, clearly explain the execution results and suggest the next steps.
                If you want to stop the interaction at any point, use the `terminate` tool/function call.
                """;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(10);
        // 初始化 AI 对话客户端
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .build();
        this.setChatClient(chatClient);
    }
}
