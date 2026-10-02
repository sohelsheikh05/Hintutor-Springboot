package com.Hintutor.Hinttutor.Controller;



import com.Hintutor.Hinttutor.Dto.NextHintRequest;
import com.Hintutor.Hinttutor.Dto.StartRequest;
import com.Hintutor.Hinttutor.Service.HintTutorService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping
public class HintTutorController {

    private final HintTutorService hintTutorService;

    public HintTutorController(
            HintTutorService hintTutorService
    ) {
        this.hintTutorService = hintTutorService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> start(
            @RequestBody StartRequest request
    ) {
        System.out.println("in start");
        Map<String, Object> response =
                hintTutorService.startSession(
                        request.getQuestion()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{id}")
    public ResponseEntity<?> getSession(
            @PathVariable UUID id
    ) {

        try {

            return ResponseEntity.ok(
                    hintTutorService.getSession(id)
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/session/{id}/next")
    public ResponseEntity<?> nextHint(
            @PathVariable UUID id,
            @RequestBody NextHintRequest request
    ) {

        try {

            Map<String, Object> response =
                    hintTutorService.nextHint(
                            id,
                            request.getUserAttempt()
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }
    @GetMapping("/session/{id}/solution")
    public ResponseEntity<?> getSolution(
            @PathVariable UUID id
    ) {

        try {

            Map<String, Object> response =
                    hintTutorService.getSolution(id);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.notFound().build();
        }
    }
}
