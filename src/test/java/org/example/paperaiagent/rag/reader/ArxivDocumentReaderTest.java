package org.example.paperaiagent.rag.reader;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisabledIf("GithubCI")
@Slf4j
class ArxivDocumentReaderTest {
    private static final String TEST_QUERY = "ti:Deep High-Resolution Representation Learning for Human Pose Estimation";

    private static final int MAX_SIZE = 2;

    @Resource
    VectorStore pgVectorVectorStore;

    /**
     * Check if the tests are running in Local. In GitHub CI environment, this test not
     * running.
     */
    static boolean GithubCI() {
        return "true".equals(System.getenv("ENABLE_TEST_CI"));
    }

    @Test
    public void testDocumentReader() {
        // Create document reader
        ArxivDocumentReader reader = new ArxivDocumentReader(TEST_QUERY, MAX_SIZE);

        // Get documents
        List<Document> documents = reader.get();

        // pgVector
//        pgVectorVectorStore.add(documents);
        List<Document> search = pgVectorVectorStore.similaritySearch("hrnet是什么？");
        List<Document> search2 = pgVectorVectorStore.similaritySearch("hrnet模型在哪些数据集上跑过？真实的实验结果是多少？");

        // Verify results
        assertFalse(documents.isEmpty(), "Should return at least one document");

        // Verify metadata of the first document
        Document firstDoc = documents.get(0);
        assertNotNull(firstDoc.getText(), "Document content should not be null");

        // Verify metadata
        var metadata = firstDoc.getMetadata();
        assertNotNull(metadata.get(ArxivResource.ENTRY_ID), "Should contain article ID");
        assertNotNull(metadata.get(ArxivResource.TITLE), "Should contain title");
        assertNotNull(metadata.get(ArxivResource.AUTHORS), "Should contain authors");
        assertNotNull(metadata.get(ArxivResource.SUMMARY), "Should contain summary");
        assertNotNull(metadata.get(ArxivResource.CATEGORIES), "Should contain categories");
        assertNotNull(metadata.get(ArxivResource.PRIMARY_CATEGORY), "Should contain primary category");
        assertNotNull(metadata.get(ArxivResource.PDF_URL), "Should contain PDF URL");

        // Verify categories
        @SuppressWarnings("unchecked")
        List<String> categories = (List<String>) metadata.get(ArxivResource.CATEGORIES);
        assertTrue(categories.contains("cs.CV"), "Should contain cs.CV category");
    }

    @Test
    public void testGetSummaries() {
        // Create document reader
        ArxivDocumentReader reader = new ArxivDocumentReader(TEST_QUERY, MAX_SIZE);

        // Get summary documents
        List<Document> documents = reader.getSummaries();

        // Verify results
        assertFalse(documents.isEmpty(), "Should return at least one document");

        // Verify the first document
        Document firstDoc = documents.get(0);

        // Verify content (summary)
        assertNotNull(firstDoc.getText(), "Document content (summary) should not be null");
        assertFalse(firstDoc.getText().trim().isEmpty(), "Document content (summary) should not be empty string");

        // Verify metadata
        var metadata = firstDoc.getMetadata();
        assertNotNull(metadata.get(ArxivResource.ENTRY_ID), "Should contain article ID");
        assertNotNull(metadata.get(ArxivResource.TITLE), "Should contain title");
        assertNotNull(metadata.get(ArxivResource.AUTHORS), "Should contain authors");
        assertNotNull(metadata.get(ArxivResource.SUMMARY), "Should contain summary");
        assertNotNull(metadata.get(ArxivResource.CATEGORIES), "Should contain categories");
        assertNotNull(metadata.get(ArxivResource.PRIMARY_CATEGORY), "Should contain primary category");
        assertNotNull(metadata.get(ArxivResource.PDF_URL), "Should contain PDF URL");

        // Verify summary content matches the summary in metadata
        assertEquals(firstDoc.getText(), metadata.get(ArxivResource.SUMMARY),
                "Document content should match the summary in metadata");
    }

}