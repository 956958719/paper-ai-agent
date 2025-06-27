package org.example.paperaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ArxivSearchToolTest {

    private ArxivSearchTool arxivSearchTool = new ArxivSearchTool();

    @Test
    void searchPapers() {
        List<ArxivSearchTool.ArxivPaperResult> result = arxivSearchTool.searchPapers("higher hrnet");
        Assertions.assertNotNull(result);
    }
}