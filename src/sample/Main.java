package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    private ObservableList<CommanderFile> itemsLeft = FXCollections.observableArrayList();
    private ObservableList<CommanderFile> itemsRight = FXCollections.observableArrayList();

    private String currentPathLeft;
    private String currentPathRight;

    private TextField pathFieldLeft;
    private TextField pathFieldRight;
    private Stage primaryStage;

    TableView<CommanderFile> leftListView;
    TableView<CommanderFile> rightListView;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Unlimited Commander by Marcin Slusarek");
        primaryStage.setScene(new Scene(root, 1000, 600));
        this.primaryStage = primaryStage;
        primaryStage.show();

        SplitPane splitPane = (SplitPane) root.lookup("#splitPane");
        splitPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        leftListView = (TableView<CommanderFile>) root.lookup("#leftListView");
        leftListView.setOnKeyPressed(k -> {
            if (k.getCode().toString().equals("F7")) {
                createNewFolderIn(currentPathLeft, true);
            } else if (k.getCode().toString().equals("F8")) {
                deleteSelectedItem(rightListView, false);
            }
        });
        leftListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (itemsLeft.get(leftListView.getSelectionModel().getSelectedIndex()).getFile().isDirectory()) {
                    setupListView(leftListView, true, itemsLeft.get(leftListView.getSelectionModel().getSelectedIndex()).getFile());
                }
            }
        });

        rightListView = (TableView<CommanderFile>) root.lookup("#rightListView");
        rightListView.setOnKeyPressed(k -> {
            if (k.getCode().toString().equals("F7")) {
                createNewFolderIn(currentPathRight, false);
            } else if (k.getCode().toString().equals("F8")) {
                deleteSelectedItem(rightListView, false);
            }
        });

        rightListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (itemsRight.get(rightListView.getSelectionModel().getSelectedIndex()).getFile().isDirectory()) {
                    setupListView(rightListView, false, itemsRight.get(rightListView.getSelectionModel().getSelectedIndex()).getFile());
                }
            }
        });

        TableColumn<CommanderFile, String> nameColumnLeft = new TableColumn<>("Name");
        TableColumn<CommanderFile, String> dateColumnLeft = new TableColumn<>("Date");
        setupColumns(nameColumnLeft, dateColumnLeft);


        TableColumn<CommanderFile, String> nameColumnRight = new TableColumn<>("Name");
        TableColumn<CommanderFile, String> dateColumnRight = new TableColumn<>("Date");
        setupColumns(nameColumnRight, dateColumnRight);

//        leftListView.setOnDragDetected(e -> {
//            String selected = leftListView.getSelectionModel().getSelectedItem().getFile().getAbsolutePath();
//            Dragboard dragboard = leftListView.startDragAndDrop(TransferMode.ANY);
//            ClipboardContent content = new ClipboardContent();
//            dragboard.setContent(content);
//            e.consume();
//            e.setDragDetect(true);
//        });
//
//        leftListView.setOnMousePressed(e -> {
//            leftListView.setMouseTransparent(true);
//            e.setDragDetect(true);
//        });
//
//        leftListView.setOnMouseDragged(e -> {
//            leftListView.setMouseTransparent(false);
//            e.setDragDetect(false);
//        });
//
//        leftListView.setOnDragOver(e -> {
//            Dragboard dragboard = e.getDragboard();
//            if (e.getDragboard().hasString()) {
//                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
//            }
//            e.consume();
//        });
//
//        leftListView.setOnDragDropped(e -> {
//            Dragboard dragboard = e.getDragboard();
//            boolean success = false;
//            if (e.getDragboard().hasString()) {
//              String text = dragboard.getString();
//            }
//        });

        leftListView.getColumns().addAll(nameColumnLeft, dateColumnLeft);
        rightListView.getColumns().addAll(nameColumnRight, dateColumnRight);

        var defaultPath = "C:/";
        if (!OsUtils.isWindows()) {
            defaultPath = "/Users/";
        }

        File folder = new File(defaultPath);
        currentPathLeft = folder.getAbsolutePath();
        currentPathRight = folder.getAbsolutePath();

        pathFieldLeft = (TextField) root.lookup("#leftPathField");
        pathFieldLeft.setEditable(false);

        pathFieldRight = (TextField) root.lookup("#rightPathField");
        pathFieldRight.setEditable(false);

        pathFieldLeft.setText(currentPathLeft);
        pathFieldRight.setText(currentPathRight);

        setupListView(leftListView, true, folder);
        setupListView(rightListView, false, folder);
    }

    private void deleteSelectedItem(TableView<CommanderFile> listView, boolean left) {
        listView.getSelectionModel().getSelectedItem().getFile().delete();

        if (left) {
            setupListView(leftListView, true, new File(currentPathLeft));
        } else {
            setupListView(rightListView, false, new File(currentPathRight));
        }
    }

    private void createNewFolderIn(String path, boolean left) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        VBox dialogVbox = new VBox(20);
        dialogVbox.setPadding(new Insets(10, 50, 50, 50));

        dialogVbox.getChildren().add(new Text("Folder name"));
        TextField folderNameInput = new TextField("Folder name");
        dialogVbox.getChildren().add(folderNameInput);
        Button createFolderButton = new Button("Create");
        createFolderButton.setOnMouseClicked(v -> {
            dialog.close();
            String folderName = folderNameInput.getText();
            File folderFile = new File(path + "/" + folderName);
            System.out.println(folderFile.getAbsolutePath());
            folderFile.mkdirs();
            if (left) {
                setupListView(leftListView, true, folderFile.getParentFile());
            } else {
                setupListView(rightListView, false, folderFile.getParentFile());
            }
        });
        dialogVbox.getChildren().add(createFolderButton);
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void setupColumns(TableColumn<CommanderFile, String> nameColumnLeft, TableColumn<CommanderFile, String> dateColumnLeft) {
        dateColumnLeft.setCellValueFactory(new PropertyValueFactory<>("date"));
        nameColumnLeft.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void setupListView(TableView<CommanderFile> tableView, boolean isLeft, File file) {
        List<File> listOfFiles = new ArrayList<>(Arrays.asList(file.listFiles()));

        if (file.getParentFile() != null) {
            listOfFiles.add(0, file.getParentFile());
        }

        if (isLeft) {
            currentPathLeft = setupFileTree(tableView, listOfFiles, file, itemsLeft);
        } else {
            currentPathRight = setupFileTree(tableView, listOfFiles, file, itemsRight);
        }
        pathFieldLeft.setText(currentPathLeft);
        pathFieldRight.setText(currentPathRight);
    }

    private String setupFileTree(TableView<CommanderFile> tableView, List<File> listOfFiles, File file, ObservableList<CommanderFile> itemList) {
        itemList.clear();
        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                System.out.println("File " + listOfFile.getName());
                itemList.add(new CommanderFile(listOfFile)); //"\uD83D\uDDC4 " + listOfFile.getName());

            } else if (listOfFile.isDirectory()) {
                System.out.println("Directory " + listOfFile.getName());
                itemList.add(new CommanderFile(listOfFile)); //"\uD83D\uDCC1 " + listOfFile.getName());
            }
        }
        if (file.getParentFile() != null) {
            itemList.set(0, new CommanderFile(file.getParentFile(), "⬅ .."));
        }

        tableView.setItems(itemList);

        return file.getAbsolutePath();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
