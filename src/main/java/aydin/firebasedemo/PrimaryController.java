package aydin.firebasedemo;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class PrimaryController {
    @FXML
    private TextField ageTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextArea outputTextArea;

    @FXML
    private Button readButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button switchSecondaryViewButton;

    @FXML
    private Button writeButton;

    private boolean key;
    private ObservableList<Person> listOfUsers = FXCollections.observableArrayList();
    private Person person;

    public ObservableList<Person> getListOfUsers() {
        return listOfUsers;
    }

    void initialize() {
        AccessDataView accessDataViewModel = new AccessDataView();
        nameTextField.textProperty().bindBidirectional(accessDataViewModel.personNameProperty());
        writeButton.disableProperty().bind(accessDataViewModel.isWritePossibleProperty().not());
    }

    @FXML
    void readButtonClicked(ActionEvent event) {
        readFirebase();
    }

    @FXML
    void registerButtonClicked(ActionEvent event) {
        registerUser();
    }

    @FXML
    void writeButtonClicked(ActionEvent event) {
        addData();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        DemoApp.setRoot("secondary");
    }

    public boolean readFirebase() {
        key = false;

        ApiFuture<QuerySnapshot> future = DemoApp.fstore.collection("Persons").get();
        List<QueryDocumentSnapshot> documents;
        try {
            documents = future.get().getDocuments();
            if(documents.size() > 0) {
                System.out.println("Getting (reading) data from firebase database....");
                listOfUsers.clear();
                outputTextArea.clear();
                for (QueryDocumentSnapshot document : documents) {
                    String name = String.valueOf(document.getData().get("Name"));
                    String age = String.valueOf(document.getData().get("Age"));
                    String phone = document.getData().get("Phone") != null ?
                            String.valueOf(document.getData().get("Phone")) : "N/A";

                    outputTextArea.setText(outputTextArea.getText() +
                            name + ", Age: " + age + ", Phone: " + phone + "\n");

                    System.out.println(document.getId() + " => " + name);
                    person = new Person(name, Integer.parseInt(age), phone);
                    listOfUsers.add(person);
                }
            } else {
                System.out.println("No data");
                outputTextArea.setText("No data in database");
            }
            key = true;

        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
        return key;
    }

    public boolean registerUser() {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail("user222@example.com")
                .setPassword("secretPassword")
                .setDisplayName("John Doe")
                .setDisabled(false);

        try {
            UserRecord userRecord = DemoApp.fauth.createUser(request);
            System.out.println("Successfully created new user with UID: " + userRecord.getUid());
            return true;

        } catch (FirebaseAuthException ex) {
            System.out.println("Error creating a new user: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public void addData() {
        DocumentReference docRef = DemoApp.fstore.collection("Persons").document(UUID.randomUUID().toString());

        Map<String, Object> data = new HashMap<>();
        data.put("Name", nameTextField.getText());
        data.put("Age", Integer.parseInt(ageTextField.getText()));
        data.put("Phone", phoneTextField.getText());

        ApiFuture<WriteResult> result = docRef.set(data);

        nameTextField.clear();
        ageTextField.clear();
        phoneTextField.clear();
    }
}