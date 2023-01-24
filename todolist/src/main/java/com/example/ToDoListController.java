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

  private File selectedFile = null;
  static String fileName = "Nuovo documento";
  private String defaultPath = "";

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

  private void writeListViewOnTxt (ListView<String> listView, File file){
    //Prelevo gli elementi all'interno della ListView
    List<String> lines = listView.getItems();
    //Prelevo il path del file che andrò a scrivere
    String path = file.getPath();
    writeDocument(lines, path);
  }

  private void writeDocument (List<String> lines, String path){
    //Provo a scrivere il file che si trova nel path fornito e a metterle nella memoria tampone
    try (FileWriter outFile = new FileWriter(path);
    BufferedWriter bWriter = new BufferedWriter(outFile)) {
      // scrivo linea per linea la lista all'interno del file
      for (String line:lines){
        bWriter.write(line);
        bWriter.newLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }    
  }

  private String saveLastAbsolutePath (File file){
    defaultPath = file.getAbsolutePath();
    defaultPath = defaultPath.replaceAll("\\\\", "/");
    int toLastSlash = defaultPath.lastIndexOf("/")+1;
    defaultPath = defaultPath.substring(0,toLastSlash);
    return defaultPath;
  }

  private File setDefaultPath (String path){
    String userDirectoryString = System.getProperty("user.home");
    File userDirectory = new File(path);
    if(!userDirectory.canRead()) {
      userDirectory = new File(userDirectoryString);
    }
    return userDirectory;
  }

  private void saveDocument (boolean changePath){
    FileChooser fileChooser = new FileChooser();
    //Filtro per le estensioni selezionabili
    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text doc(*.txt)","*.txt"));
    //Titolo per la finestra di dialogo
    fileChooser.setTitle("Salva con nome...");
    try{
      //verifica se voglio salvare su un file preesistente o se voglio salvare su un nuovo file
      if (selectedFile != null && changePath == false) {
        writeListViewOnTxt(lvTBD,selectedFile);
      }else{
        //Richiama l'ultimo percorso selezionato per un salvataggio o un'apertura
        File userDirectory = setDefaultPath(defaultPath);
        fileChooser.setInitialDirectory(userDirectory);
        //Mostra la finestra di dialogo per il salvataggio
        selectedFile = fileChooser.showSaveDialog(null).getAbsoluteFile();
        //Imposta un nuovo percorso di default in base all'ultimo percorso utilizzato
        defaultPath = saveLastAbsolutePath(selectedFile);
        if(!selectedFile.getName().contains(".")) {
          selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
        }
        writeListViewOnTxt(lvTBD,selectedFile);
        fileName = selectedFile.getName();
        changeTitle();
      }
    }catch (RuntimeException e){
    }
  }

  private void saveConfig (){

  }

  @FXML
  void OnClickMenuOpen(ActionEvent event) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Apri il file...");
    File selectedFileChange = null;
    //Filtro per le estensioni selezionabili
    FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("files TXT (*.txt)", "*.txt");
    fileChooser.getExtensionFilters().add(extensionFilter);
    //Imposta un indirizzo di default, o vai alla user directory se l'indirizzo non è valido
    File userDirectory = setDefaultPath(defaultPath);
    //Seleziona il file da leggere
    fileChooser.setInitialDirectory(userDirectory);
    //Verifica che sia selezionato un file
    selectedFileChange = fileChooser.showOpenDialog(null);
    if (selectedFileChange != null) {
        try {
          //Salva l'ultimo path visitato come path predefinito
          saveLastAbsolutePath(selectedFileChange);
          //Memorizza l'ultimo file aperto nella variabile globale selectedFile
          selectedFile = selectedFileChange;
          //Legge il file riga per riga
          List<String> lines = Files.readAllLines(selectedFile.toPath());
          //estrapola il nome del documento aperto e lo salva nella variabile globale fileName
          fileName = selectedFile.getName();
          //Aggiorna il titolo della scena aggiungendo il nome del documento aperto
          changeTitle();
          //Svuota i due list view e aggiorna lvTBD con gli elementi letti dal file
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
