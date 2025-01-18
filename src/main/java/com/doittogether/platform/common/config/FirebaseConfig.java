package com.doittogether.platform.common.config;

import com.doittogether.platform.application.global.code.ExceptionCode;
import com.doittogether.platform.application.global.exception.GlobalException;
import com.doittogether.platform.application.global.exception.fcm.FcmException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
public class FirebaseConfig {

    private final String firebaseSdkJsonPath;

    public FirebaseConfig(
            @Value("${firebase.sdk-jsonPath}") String firebaseSdkJsonPath) {
        this.firebaseSdkJsonPath = firebaseSdkJsonPath;
    }

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        log.info("Initializing FirebaseApp with SDK JSON path: {}", firebaseSdkJsonPath);
        // FirebaseApp이 이미 초기화되어 있는지 확인
        List<FirebaseApp> apps = FirebaseApp.getApps();
        if (!apps.isEmpty()) {
            log.info("FirebaseApp is already initialized. Returning the existing instance.");
            return FirebaseApp.getInstance(); // 이미 초기화된 FirebaseApp 반환
        }

        // Firebase 초기화
        try (FileInputStream serviceAccount = new FileInputStream(firebaseSdkJsonPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            log.info("FirebaseApp initialization successful.");
            return FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            log.error("Failed to initialize FirebaseApp. Ensure the SDK JSON path is correct: {}", firebaseSdkJsonPath, e);

            throw new FcmException(ExceptionCode.FCM_INITIALIZATION_FAILED);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
