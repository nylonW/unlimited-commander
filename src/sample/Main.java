package sample;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;

public class Main extends Application {

    private ObservableList<CommanderFile> itemsLeft = FXCollections.observableArrayList();
    private ObservableList<CommanderFile> itemsRight = FXCollections.observableArrayList();

    private String currentPathLeft;
    private String currentPathRight;

    private TextField pathFieldLeft;
    private TextField pathFieldRight;
    private Stage primaryStage;

    private TableView<CommanderFile> leftListView;
    private TableView<CommanderFile> rightListView;

    private final ObjectProperty<TableRow<CommanderFile>> dragSource = new SimpleObjectProperty<>();


    @Override
    public void start(Stage primaryStage) throws Exception {
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
                deleteSelectedItem(leftListView, true);
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

        setupDragAndDrop(leftListView);
        setupDragAndDrop(rightListView);

        setOnDragDropped(leftListView, true);
        setOnDragDropped(rightListView, false);

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

    private void setupDragAndDrop(TableView<CommanderFile> tableView) {
        tableView.setRowFactory(lv -> {
            TableRow<CommanderFile> cell = new TableRow<>(){
                @Override
                public void updateItem(CommanderFile item , boolean empty) {
                    super.updateItem(item, empty);
                }
            };

            cell.setOnDragDetected(event -> {
                if (!cell.isEmpty()) {
                    Dragboard db = cell.startDragAndDrop(TransferMode.ANY);
                    ClipboardContent cc = new ClipboardContent();
                    cc.putString(cell.getItem().getFile().getAbsolutePath());
                    db.setContent(cc);
                    dragSource.set(cell);
                }
            });

            cell.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasString()) {
                    event.acceptTransferModes(TransferMode.ANY);

                }
            });

            return cell ;
        });
    }

    private void setOnDragDropped(TableView<CommanderFile> leftListView, boolean left) {
        leftListView.setOnDragDropped(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();

            String currentPath;
            if (left) {
                currentPath = currentPathLeft;
            } else {
                currentPath = currentPathRight;
            }

            File dragFile = dragSource.get().getItem().getFile();

            try {
                if (dragFile.isDirectory()) {
                    FileUtils.copyDirectory(dragFile, new File(currentPath + "/" + dragFile.getName()));
                } else {
                    FileUtils.copyFile(dragFile, new File(currentPath + "/" + dragFile.getName()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            refreshLists();
        });
    }

    private void deleteSelectedItem(TableView<CommanderFile> listView, boolean left) {
        try {
            if (listView.getSelectionModel().getSelectedItem().getFile().isDirectory()) {
                FileUtils.deleteDirectory(listView.getSelectionModel().getSelectedItem().getFile());
            } else {
                listView.getSelectionModel().getSelectedItem().getFile().delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        refreshLists();
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


    private void refreshLists() {
        setupListView(leftListView, true, new File(currentPathLeft));
        setupListView(rightListView, false, new File(currentPathRight));
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
