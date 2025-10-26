package aydin.firebasedemo;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.InputStream;
import java.io.IOException;

public class FirestoreContext {

    private static Firestore firestore;

    public static Firestore getFirestore() {
        if (firestore == null) {
            try (InputStream serviceAccount = FirestoreContext.class
                    .getClassLoader()
                    .getResourceAsStream("ServiceAccountKey.json")) {

                if (serviceAccount == null) {
                    throw new IOException("ServiceAccountKey.json not found in resources folder.");
                }

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .setProjectId("module-7-lab-a0045")
                            .build();
                    FirebaseApp.initializeApp(options);
                }

                firestore = FirestoreClient.getFirestore();

            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Failed to initialize Firestore", ex);
            }
        }
        return firestore;
    }
}