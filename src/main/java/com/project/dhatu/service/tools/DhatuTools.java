package com.project.dhatu.service.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DhatuTools {

    private final JdbcTemplate jdbcTemplate;

    public DhatuTools(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Tool(description = "Returns the current date and time. Use this when the user asks what today's date is or what the current time is.")
    public String getCurrentDateTime() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy 'at' HH:mm"));
    }

    @Tool(description = "Returns how many document chunks are currently indexed in the Dhatu knowledge base. Use this when the user asks how many documents or how much knowledge is available.")
    public String getKnowledgeBaseStats() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM vector_store", Integer.class);
            return "The Dhatu knowledge base currently contains "
                    + count + " indexed document chunks.";
        } catch (Exception e) {
            return "Knowledge base statistics are currently unavailable.";
        }
    }
}
