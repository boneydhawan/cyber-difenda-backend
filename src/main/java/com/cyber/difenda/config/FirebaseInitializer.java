package com.cyber.difenda.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FirebaseInitializer {
    @PostConstruct
    public void init() throws IOException {
    	InputStream serviceAccount = getClass().getClassLoader()
                .getResourceAsStream("serviceAccountKeyFirebase/CD_firebaseKey.json");

        if (serviceAccount == null) {
            throw new RuntimeException("Firebase serviceAccountKey.json not found in resources!");
        }
        
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
