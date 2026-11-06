package com.hospital.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String analyzeSymptoms(String symptoms) {

        String systemPrompt = """
                You are a healthcare information assistant
                integrated into a hospital management system.

                Analyze the symptoms provided by the patient and
                respond with the following four sections:

                1. Possible Conditions:
                   Mention 3-4 possible explanations, without
                   claiming that any of them is a diagnosis.

                2. Recommended Medical Department:
                   Suggest an appropriate medical department
                   such as General Medicine, Cardiology or Neurology.

                3. Urgency Level:
                   Classify the situation as LOW, MEDIUM or HIGH.
                   Do not present this classification as a
                   clinically verified assessment.

                4. General Advice:
                   Provide safe, general guidance.
                   Do not prescribe medicines or specific dosages.

                Important safety instructions:
                - Never claim to provide a medical diagnosis.
                - Clearly state that the response is AI-generated
                  informational guidance.
                - Recommend consultation with a qualified doctor.
                - If symptoms suggest a potentially life-threatening
                  emergency, advise immediate emergency medical care.
                - Do not reassure a patient that serious symptoms
                  are harmless.

                Keep the response concise and easy to understand.
                """;

        String analysis = chatClient.prompt()
                .system(systemPrompt)
                .user(symptoms)
                .call()
                .content();

        if (analysis == null || analysis.isBlank()) {
            throw new IllegalStateException(
                    "The AI service returned an empty response"
            );
        }

        return analysis;
    }
}