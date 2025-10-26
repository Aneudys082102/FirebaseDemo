package aydin.firebasedemo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;

public class DemoApp extends Application {
    public static Scene scene;
    public static Firestore fstore;
    public static FirebaseAuth fauth;

    @Override
    public void start(Stage stage) throws IOException {
        fstore = FirestoreContext.getFirestore();
        fauth = FirebaseAuth.getInstance();

        scene = new Scene(loadFXML("welcome"), 500, 450);
        stage.setScene(scene);
        stage.setTitle("Firebase Demo");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                DemoApp.class.getResource("/aydin/firebasedemo/" + fxml + ".fxml")
        );
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}