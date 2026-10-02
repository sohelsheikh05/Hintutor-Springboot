package com.Hintutor.Hinttutor.Service;


import com.Hintutor.Hinttutor.Model.HintSession;
import com.Hintutor.Hinttutor.Repository.HintSessionRepository;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class HintTutorService {

    private final ChatClient chatClient;
    private final HintSessionRepository sessionRepository;

    public HintTutorService(
            ChatClient chatClient,
            HintSessionRepository sessionRepository
    ) {
        this.chatClient = chatClient;
        this.sessionRepository = sessionRepository;
    }

    private static final String SYSTEM_PROMPT = """
            You are HintTutor, an AI tutor.

            Your job is to help students solve programming
            and technical problems through hints.

            Rules:
            1. Give exactly one hint at a time.
            2. Do not provide the complete solution.
            3. Ask guiding questions when useful.
            4. Keep the hint short, between 1 and 3 sentences.
            5. Encourage the student to think independently.
            """;

    public Map<String, Object> startSession(String question) {

        if (question == null || question.isBlank()) {

            throw new IllegalArgumentException(
                    "Question cannot be empty"
            );
        }
        System.out.println("instatsession");
        HintSession session = new HintSession(question);
        String hint="";
        try {
             hint = chatClient
                    .prompt()
                    .system(SYSTEM_PROMPT)
                    .user(question)
                    .call()
                    .content();
        }
        catch (Exception e){
            System.out.println(e);
        }
        session.addMessage("Student question: " + question);
        session.addMessage("Tutor hint: " + hint);
        session.incrementHintIndex();
        System.out.println("instatsession"+hint);
        sessionRepository.save(session);

        return Map.of(
                "sessionId", session.getId(),
                "question", session.getQuestion(),
                "hint", hint,
                "hintIndex", session.getHintIndex(),
                "tokensUsed", session.getTokensUsed()
        );
    }
    public Map<String, Object> nextHint(
            UUID sessionId,
            String userAttempt
    ) {

        HintSession session = sessionRepository.findById(sessionId);

        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        if (session.getHintIndex() >= 10) {

            return Map.of(
                    "sessionId", session.getId(),
                    "hint", "You have reached the maximum number of hints.",
                    "hintIndex", session.getHintIndex(),
                    "done", true
            );
        }

        if (userAttempt == null || userAttempt.isBlank()) {
            userAttempt = "The student has not provided an attempt yet.";
        }

        session.addMessage("Student attempt: " + userAttempt);

        String conversation = String.join(
                "\n",
                session.getConversationHistory()
        );

        String prompt = """
            Original question:
            %s

            Conversation history:
            %s

            Generate the next hint.

            Rules:
            1. Give exactly one hint.
            2. Do not provide the complete solution.
            3. Keep it between 1 and 3 sentences.
            4. Guide the student based on their attempt.
            5. Encourage independent thinking.
            """.formatted(
                session.getQuestion(),
                conversation
        );

        String hint = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(prompt)
                .call()
                .content();

        session.addMessage("Tutor hint: " + hint);
        session.incrementHintIndex();

        boolean done = hint != null &&
                hint.toLowerCase().contains("done");

        sessionRepository.save(session);

        return Map.of(
                "sessionId", session.getId(),
                "hint", hint,
                "hintIndex", session.getHintIndex(),
                "tokensUsed", session.getTokensUsed(),
                "done", done
        );
    }
    public HintSession getSession(UUID sessionId) {

        HintSession session = sessionRepository.findById(sessionId);

        if (session == null) {

            throw new IllegalArgumentException(
                    "Session not found"
            );
        }

        session.updateActivity();

        return session;
    }
    public Map<String, Object> getSolution(UUID sessionId) {

        HintSession session =
                sessionRepository.findById(sessionId);

        if (session == null) {
            throw new IllegalArgumentException("Session not found");
        }

        String conversation = String.join(
                "\n",
                session.getConversationHistory()
        );

        String prompt = """
            Original question:
            %s

            Previous conversation:
            %s

            Provide a complete and clear solution.

            Requirements:
            1. Explain the approach step by step.
            2. Include code when appropriate.
            3. Explain the time and space complexity.
            4. Correct any misunderstandings in the student's attempts.
            5. Use beginner-friendly language. 
            """.formatted(
                session.getQuestion(),
                conversation
        );

        String solution = chatClient
                .prompt()
                .system("""
                    You are HintTutor.

                    The student has requested the complete solution.
                    You may now provide the full explanation.
                    """)
                .user(prompt)
                .call()
                .content();

        session.updateActivity();

        return Map.of(
                "sessionId", session.getId(),
                "question", session.getQuestion(),
                "solution", solution,
                "hintCount", session.getHintIndex()
        );
    }
}
