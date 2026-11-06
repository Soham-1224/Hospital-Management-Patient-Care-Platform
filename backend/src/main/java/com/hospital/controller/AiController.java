package com.hospital.controller;

import com.hospital.dto.SymptomAnalysisRequestDTO;
import com.hospital.dto.SymptomAnalysisResponseDTO;
import com.hospital.service.GeminiService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.retry.TransientAiException;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private static final Logger logger =
            LoggerFactory.getLogger(AiController.class);

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/analyze-symptoms")
    public ResponseEntity<?> analyzeSymptoms(
            @Valid @RequestBody SymptomAnalysisRequestDTO request) {

        long startTime = System.nanoTime();

        logger.info("AI symptom analysis request received");

        try {

            String analysis = geminiService.analyzeSymptoms(
                    request.getSymptoms()
            );

            long durationMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            SymptomAnalysisResponseDTO response =
                    new SymptomAnalysisResponseDTO(analysis);

            logger.info(
                    "AI symptom analysis completed successfully. Duration: {} ms",
                    durationMs
            );

            return ResponseEntity.ok(response);

        } catch (TransientAiException e) {

            long durationMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            logger.warn(
                    "Temporary AI provider failure. Duration: {} ms",
                    durationMs
            );

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "message",
                            "AI service is temporarily unavailable. Please try again later."
                    ));

        } catch (NonTransientAiException e) {

            long durationMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            logger.warn(
                    "Non-transient AI provider failure. Duration: {} ms",
                    durationMs
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "message",
                            "The AI service could not process the request. Please contact support if the problem persists."
                    ));

        } catch (Exception e) {

            long durationMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            logger.error(
                    "Unexpected AI symptom analysis failure. Duration: {} ms",
                    durationMs,
                    e
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message",
                            "An unexpected error occurred while processing your request."
                    ));
        }
    }
}