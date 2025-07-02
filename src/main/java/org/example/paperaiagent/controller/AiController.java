package org.example.paperaiagent.controller;

import jakarta.annotation.Resource;

import org.example.paperaiagent.agent.PaperManus;
import org.example.paperaiagent.app.PaperHelper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

@RestController
@RequestMapping("/ai")
public class AiController {
    @Resource
    private PaperHelper paperHelper;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用AI恋爱大师应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("paper_helper/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return paperHelper.doChat(message, chatId);
    }

    /**
     * SSE 调用AI恋爱大师应用（方式一）
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "paper_helper/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return paperHelper.doChatByStream(message, chatId);
    }

    /**
     * SSE 调用AI恋爱大师应用（方式二，这种方式就不需要自己设置响应头produces）
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "paper_helper/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId) {
        return paperHelper.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 调用AI恋爱大师应用（方式三，这种方式也不需要自己设置响应头produces）
     * 每次调用send都会主动发送一次消息
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "paper_helper/chat/sse_emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3 分钟超时
        // 获取 FLUX 响应式数据并且直接通过订阅链接推送给 SseEmitter
        paperHelper.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        return sseEmitter;
    }

    @Resource
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * 流式调用 manus 超级智能体
     *
     * @param message
     * @return
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        PaperManus yuManus = new PaperManus(allTools, toolCallbackProvider, dashscopeChatModel);
        return yuManus.runStream(message);
    }
}
