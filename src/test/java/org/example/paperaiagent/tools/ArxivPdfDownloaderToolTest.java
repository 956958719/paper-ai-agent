package org.example.paperaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArxivPdfDownloaderToolTest {

    @Test
    void downloadArxivPdf() {
        ArxivPdfDownloaderTool arxivPdfDownloaderTool = new ArxivPdfDownloaderTool();
        arxivPdfDownloaderTool.downloadArxivPaper("2208.13944");
    }
}