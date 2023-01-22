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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ToDoListController {

  File selectedFile = null;
  static String fileName = "Nuovo documento";
  File defaultPath = null; // devo trovare il modo di settarlo come ultimo path visitato

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

  private void changeTitle() {
    Stage stage = (Stage) tfNewTask.getScene().getWindow();
    stage.setTitle("To do list - "+fileName);
  }

  private void writeDocument (){
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

  private void saveDocument (boolean changePath){
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)","*.txt"));
    fileChooser.setTitle("Salva con nome...");
    if (selectedFile != null && changePath == false) {
      writeDocument();
    }else{
      selectedFile = fileChooser.showSaveDialog(null).getAbsoluteFile();
      if(!selectedFile.getName().contains(".")) {
        selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
      }
      writeDocument();
      fileName = selectedFile.getName();
      changeTitle();
    }
  }

  @FXML
  void OnClickMenuOpen(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Apri il file...");
    File selectedFileChange = null;
    /*if (selectedFile != null){      
      fileChooser.setInitialDirectory(selectedFile.getAbsoluteFile());   // devo ancora trovare il modo di fargli prendere il path dell'ultimo file aperto
    }*/
    selectedFileChange = fileChooser.showOpenDialog(null);
    if (selectedFileChange != null) {
        try {
          selectedFile = selectedFileChange;
          List<String> lines = Files.readAllLines(selectedFile.toPath());
          fileName = selectedFile.getName();
          changeTitle();
          lvTBD.getItems().clear();
          lvDone.getItems().clear();
          lvTBD.getItems().addAll(lines);
        }catch (IOException e) {
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
    saveDocument(false);
  }
  
  @FXML
  void onClickMenuSaveWithName(ActionEvent event) {
    saveDocument(true);
  }

  @FXML
  void onClickUndoneBt(ActionEvent event) {
    String item = lvDone.getSelectionModel().getSelectedItem();
    int index = lvDone.getSelectionModel().getSelectedIndex();
    lvDone.getItems().remove(index);
    lvTBD.getItems().add(item);
  }

  @FXML
  void OnClickMenuInfo(ActionEvent event) throws IOException {
    Parent root = FXMLLoader.load(getClass().getResource("todolist-info.fxml"));
    Scene scene = new Scene(root);
    Stage primaryStage = new Stage();
    primaryStage.setTitle("Info");
    primaryStage.setScene(scene);
    primaryStage.initModality(Modality.NONE);
    primaryStage.setResizable(false);
    primaryStage.initStyle(StageStyle.UTILITY);
    primaryStage.show();
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
