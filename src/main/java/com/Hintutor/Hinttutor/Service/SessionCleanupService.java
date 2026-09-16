package com.Hintutor.Hinttutor.Service;


import com.Hintutor.Hinttutor.Repository.HintSessionRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SessionCleanupService {

    private final HintSessionRepository sessionRepository;

    public SessionCleanupService(
            HintSessionRepository sessionRepository
    ) {
        this.sessionRepository = sessionRepository;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanupInactiveSessions() {

        sessionRepository.deleteInactiveSessions();
    }
}