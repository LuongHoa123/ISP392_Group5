package com.ISP392.demo.api;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
public class DialogflowController {
    private static final String PROJECT_ID = "veritasent-whxl";
    private static final String LANGUAGE_CODE = "vi";

    @PostMapping("/generate_response")
    public Map<String, String> generateResponse(@RequestBody Map<String, String> requestBody) {
        Map<String, String> responseMap = new HashMap<>();

        try {
            String sessionId = UUID.randomUUID().toString();
            String message = requestBody.get("message");


            SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                    .setCredentialsProvider(() ->
                            GoogleCredentials.fromStream(getClass().getClassLoader().getResourceAsStream("key.json"))
                    )
                    .build();

            SessionsClient sessionsClient = SessionsClient.create(sessionsSettings);
            SessionName session = SessionName.of(PROJECT_ID, sessionId);

            TextInput textInput = TextInput.newBuilder()
                    .setText(message)
                    .setLanguageCode(LANGUAGE_CODE)
                    .build();

            QueryInput queryInput = QueryInput.newBuilder()
                    .setText(textInput)
                    .build();

            DetectIntentRequest detectIntentRequest = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .build();

            DetectIntentResponse response = sessionsClient.detectIntent(detectIntentRequest);
            QueryResult queryResult = response.getQueryResult();
            String fulfillmentText = queryResult.getFulfillmentText();

            String createdDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            responseMap.put("message", fulfillmentText);
            responseMap.put("createdDate", createdDate);

        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put("error", e.getMessage());
        }

        return responseMap;
    }
}
