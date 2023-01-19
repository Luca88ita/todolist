package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class ToDoListController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BtEmptyDoneList;

    @FXML
    private Button btAdd;

    @FXML
    private Button btDone;

    @FXML
    private Button btUndone;
    
    @FXML
    private ListView<String> lvDone;

    @FXML
    private ListView<String> lvTBD;

    @FXML
    private TextField tfNewTask;
    
    File selectedFile;

    @FXML
    void OnClickMenuOpen(ActionEvent event) {
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Open Resource File");
      selectedFile = fileChooser.showOpenDialog(null);
      if (selectedFile != null) {
          try {
              List<String> lines = Files.readAllLines(selectedFile.toPath());
              lvTBD.getItems().clear();
              lvDone.getItems().clear();
              lvTBD.getItems().addAll(lines);
          } catch (IOException e) {
              Alert a = new Alert(Alert.AlertType.ERROR, "Read error");
              a.showAndWait();
          }
      }

    }

    @FXML
    void onClickAddBt(ActionEvent event) {
      String item = tfNewTask.getText();
      lvTBD.getItems().add(item);
      tfNewTask.clear();
    }

    @FXML
    void onClickDoneBt(ActionEvent event) {
      String item = lvTBD.getSelectionModel().getSelectedItem();
      int index = lvTBD.getSelectionModel().getSelectedIndex();
      lvTBD.getItems().remove(index);
      lvDone.getItems().add(item);
    }

    @FXML
    void onClickEmptyDoneListBt(ActionEvent event) {
      lvDone.getItems().clear();
    }

    @FXML
    void onClickMenuClose(ActionEvent event) {
      System.exit(0);
    }

    @FXML
    void onClickMenuSave(ActionEvent event) {
      if (selectedFile != null) {
        List<String> lines = lvTBD.getItems();
        String path = selectedFile.getPath();
        try (FileWriter outFile = new FileWriter(path);
				BufferedWriter bWriter = new BufferedWriter(outFile)) {
          for (String line:lines){
            bWriter.write(line);
            bWriter.newLine();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }    
      }
    }

    @FXML
    void onClickUndoneBt(ActionEvent event) {
      String item = lvDone.getSelectionModel().getSelectedItem();
      int index = lvDone.getSelectionModel().getSelectedIndex();
      lvDone.getItems().remove(index);
      lvTBD.getItems().add(item);
    }

    @FXML
    void initialize() {
        assert BtEmptyDoneList != null : "fx:id=\"BtEmptyDoneList\" was not injected: check your FXML file 'todolist-view.fxml'.";
        assert btAdd != null : "fx:id=\"btAdd\" was not injected: check your FXML file 'todolist-view.fxml'.";
        assert btDone != null : "fx:id=\"btDone\" was not injected: check your FXML file 'todolist-view.fxml'.";
        assert btUndone != null : "fx:id=\"btUndone\" was not injected: check your FXML file 'todolist-view.fxml'.";
        assert lvDone != null : "fx:id=\"lvDone\" was not injected: check your FXML file 'todolist-view.fxml'.";
        assert lvTBD != null : "fx:id=\"lvTBD\" was not injected: check your FXML file 'todolist-view.fxml'.";
    }

}
