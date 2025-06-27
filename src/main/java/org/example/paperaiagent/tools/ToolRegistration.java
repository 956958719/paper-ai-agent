package org.example.paperaiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Bean
    public ToolCallback[] allTools() {
        ArxivPdfDownloaderTool arxivPdfDownloaderTool = new ArxivPdfDownloaderTool();
        PdfReaderTool pdfReaderTool = new PdfReaderTool();
        TerminateTool terminateTool = new TerminateTool();
        ArxivSearchTool arxivSearchTool = new ArxivSearchTool();
        return ToolCallbacks.from(
                arxivSearchTool
                , arxivPdfDownloaderTool
                , pdfReaderTool
                , terminateTool
        );
    }
}
