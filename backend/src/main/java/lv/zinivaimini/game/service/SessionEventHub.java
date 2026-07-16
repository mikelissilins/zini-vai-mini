package lv.zinivaimini.game.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lv.zinivaimini.game.web.dto.SessionDtos.SessionView;

@Component
public class SessionEventHub {
    private final Map<String, List<SseEmitter>> listeners = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String token, SessionView snapshot) {
        SseEmitter emitter = new SseEmitter(0L);
        listeners.computeIfAbsent(token, ignored -> new CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> remove(token, emitter));
        emitter.onTimeout(() -> remove(token, emitter));
        emitter.onError(ignored -> remove(token, emitter));
        send(token, emitter, snapshot);
        return emitter;
    }

    public void publish(String token, SessionView snapshot) {
        listeners.getOrDefault(token, List.of()).forEach(emitter -> send(token, emitter, snapshot));
    }

    private void send(String token, SseEmitter emitter, SessionView snapshot) {
        try {
            emitter.send(SseEmitter.event().name("snapshot").data(snapshot));
        } catch (IOException exception) {
            emitter.complete();
            remove(token, emitter);
        }
    }

    private void remove(String token, SseEmitter emitter) {
        List<SseEmitter> emitters = listeners.get(token);
        if (emitters == null) return;
        emitters.remove(emitter);
        if (emitters.isEmpty()) listeners.remove(token);
    }
}
