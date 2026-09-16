package com.Hintutor.Hinttutor.Repository;


import com.Hintutor.Hinttutor.Model.HintSession;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class HintSessionRepository {

    private final Map<UUID, HintSession> sessions =
            new ConcurrentHashMap<>();

    public HintSession save(HintSession session) {

        sessions.put(session.getId(), session);

        return session;
    }

    public HintSession findById(UUID id) {

        return sessions.get(id);
    }

    public void deleteById(UUID id) {

        sessions.remove(id);
    }

    public boolean existsById(UUID id) {

        return sessions.containsKey(id);
    }
    public void deleteInactiveSessions() {

        LocalDateTime expiryTime =
                LocalDateTime.now().minusHours(1);

        sessions.entrySet().removeIf(entry ->
                entry.getValue()
                        .getLastActive()
                        .isBefore(expiryTime)
        );
    }
}