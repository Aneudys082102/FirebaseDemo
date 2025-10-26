package aydin.firebasedemo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.DocumentSnapshot;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class WelcomeController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    // Register new user
    @FXML
    private void handleRegister() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        System.out.println("Register button clicked!");
        System.out.println("Email: " + email);
        System.out.println("Password length: " + password.length());

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        if (!email.contains("@")) {
            showAlert("Error", "Please enter a valid email address");
            return;
        }

        if (password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long");
            return;
        }

        try {
            System.out.println("Creating user...");

            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password);

            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

            System.out.println("User created with UID: " + userRecord.getUid());

            // Save password in Firestore (for lab purposes only)
            Firestore db = FirestoreContext.getFirestore();
            Map<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("password", password);
            db.collection("UserPasswords").document(userRecord.getUid()).set(data);

            System.out.println("Password saved to Firestore");

            showAlert("Success", "User registered successfully!\nEmail: " + email);
            emailField.clear();
            passwordField.clear();

        } catch (FirebaseAuthException e) {
            System.err.println("FirebaseAuthException: " + e.getMessage());
            e.printStackTrace();

            // Handle specific error cases
            String errorMessage = e.getMessage();
            if (errorMessage.contains("EMAIL_EXISTS")) {
                showAlert("Error", "This email is already registered");
            } else if (errorMessage.contains("INVALID_EMAIL")) {
                showAlert("Error", "Invalid email format");
            } else {
                showAlert("Error", "Registration failed: " + errorMessage);
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Unexpected error: " + e.getMessage());
        }
    }

    @FXML
    private void handleSignIn() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        System.out.println("Sign In button clicked!");

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        try {
            // Look up user by email
            UserRecord user = FirebaseAuth.getInstance().getUserByEmail(email);

            System.out.println("User found: " + user.getUid());

            // Retrieve saved password from Firestore
            Firestore db = FirestoreContext.getFirestore();
            DocumentSnapshot document = db.collection("UserPasswords")
                    .document(user.getUid())
                    .get()
                    .get();

            if (document.exists()) {
                String savedPassword = document.getString("password");

                if (savedPassword != null && savedPassword.equals(password)) {
                    System.out.println("Sign in successful!");
                    showAlert("Success", "Signed in as: " + email);
                    // Switch to data access screen
                    DemoApp.setRoot("primary");
                } else {
                    showAlert("Error", "Invalid password");
                }
            } else {
                showAlert("Error", "User data not found in database");
            }

        } catch (FirebaseAuthException e) {
            System.err.println("FirebaseAuthException: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "User not found. Please register first.");
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Sign in failed: " + e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}