package org.example.paperaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.example.paperaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

public class ArxivPdfDownloaderTool {

    @Tool(
            name = "downloadArxivPaper",
            description = "Downloads a research paper PDF from arXiv using its paper ID. " +
                    "Supports IDs with versions (e.g., '1908.05806v2'). " +
                    "Returns the local file path where the PDF was saved."
    )
    public String downloadArxivPaper(
            @ToolParam(
                    description = "ArXiv ID in various formats: '1908.05806v2', 'arXiv:1908.05806v2', or full URL"
            ) String arxivId
    ) {
        try {
            // 1. 清理和标准化论文ID
            String cleanPaperId = cleanPaperId(arxivId);

            // 2. 构造 PDF URL
            String pdfUrl = "https://arxiv.org/pdf/" + cleanPaperId;

            // 3. 设置输出路径
            String savePath = buildSavePath(cleanPaperId);

            // 4. 使用 Hutool 下载文件
            return downloadWithHutool(pdfUrl, savePath);
        } catch (Exception e) {
            return "Error downloading PDF: " + e.getMessage();
        }
    }

    private String cleanPaperId(String input) {
        // 移除可能的前缀和空格
        String cleanId = input.replaceFirst("(?i)arxiv:", "")
                .replaceFirst("https?://arxiv.org/abs/", "")
                .trim();

        // 验证格式：YYYY.MMDDvN 或 YYYY.MMDD
        if (!cleanId.matches("\\d{4,5}\\.\\d{5}(v\\d+)?")) {
            throw new IllegalArgumentException("Invalid arXiv ID format. Expected format like '1908.05806v2', got: " + input);
        }
        return cleanId;
    }

    private String buildSavePath(String paperId) {
        return buildSavePath(paperId, FileConstant.FILE_SAVE_DIR + "/pdf");
    }

    private String buildSavePath(String paperId, String customDir) {
        String outputDir = StrUtil.isNotBlank(customDir) ? customDir : FileConstant.FILE_SAVE_DIR + "/pdf";

        // 移除特殊字符创建安全文件名
        String safeFileName = paperId.replaceAll("[^a-zA-Z0-9\\.]", "_") + ".pdf";

        return outputDir + File.separator + safeFileName;
    }

    private String downloadWithHutool(String pdfUrl, String savePath) {
        // 创建父目录（如果不存在）
        FileUtil.mkParentDirs(savePath);

        // 执行下载（自动处理重定向）
        try (HttpResponse response = HttpRequest.get(pdfUrl)
                .setFollowRedirects(true)
                .header("User-Agent", "Mozilla/5.0 (compatible; PaperAI/1.0)")
                .execute()) {

            if (response.isOk()) {
                // 保存文件
                FileUtil.writeBytes(response.bodyBytes(), savePath);
                return savePath;
            } else {
                // 尝试不带版本号重试（部分论文可能没有版本号PDF）
                if (pdfUrl.contains("v")) {
                    String baseUrl = pdfUrl.substring(0, pdfUrl.lastIndexOf('v'));
                    return retryDownload(baseUrl, savePath);
                }
                throw new RuntimeException("HTTP error: " + response.getStatus() + " - " + response.body());
            }
        }
    }

    private String retryDownload(String baseUrl, String savePath) {
        try (HttpResponse response = HttpRequest.get(baseUrl + ".pdf")
                .setFollowRedirects(true)
                .header("User-Agent", "Mozilla/5.0 (compatible; PaperAI/1.0)")
                .execute()) {

            if (response.isOk()) {
                FileUtil.writeBytes(response.bodyBytes(), savePath);
                return savePath + " (versionless)";
            }
            throw new RuntimeException("HTTP error on retry: " + response.getStatus());
        }
    }
}
