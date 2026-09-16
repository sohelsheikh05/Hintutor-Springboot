package com.Hintutor.Hinttutor.Model;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HintSession {
    private static final int MAX_CONTEXT_MESSAGES = 10;
    private final List<String> conversationHistory = new ArrayList<>();
    private final UUID id;
    private final String question;

    private int hintIndex;
    private int tokensUsed;

    private LocalDateTime createdAt;
    private LocalDateTime lastActive;

    public HintSession(String question) {

        this.id = UUID.randomUUID();
        this.question = question;

        this.hintIndex = 0;
        this.tokensUsed = 0;

        this.createdAt = LocalDateTime.now();
        this.lastActive = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public int getHintIndex() {
        return hintIndex;
    }

    public int getTokensUsed() {
        return tokensUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void incrementHintIndex() {
        this.hintIndex++;
        updateActivity();
    }

    public void addTokens(int tokens) {
        this.tokensUsed += tokens;
    }
    public List<String> getConversationHistory() {
        return conversationHistory;
    }

    public void addMessage(String message) {

        conversationHistory.add(message);

        if (conversationHistory.size() > MAX_CONTEXT_MESSAGES) {

            // Preserve the original student question
            String firstMessage = conversationHistory.get(0);

            List<String> recentMessages =
                    new ArrayList<>(
                            conversationHistory.subList(
                                    conversationHistory.size() - 9,
                                    conversationHistory.size()
                            )
                    );

            conversationHistory.clear();

            conversationHistory.add(firstMessage);
            conversationHistory.addAll(recentMessages);
        }
    }
    public void updateActivity() {
        this.lastActive = LocalDateTime.now();
    }
}