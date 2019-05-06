package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.File;
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

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Unlimited Commander by Marcin Slusarek");
        primaryStage.setScene(new Scene(root, 1000, 600));

        primaryStage.show();

        SplitPane splitPane = (SplitPane) root.lookup("#splitPane");
        splitPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        TableView<CommanderFile> leftListView = (TableView<CommanderFile>) root.lookup("#leftListView");
        leftListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (itemsLeft.get(leftListView.getSelectionModel().getSelectedIndex()).getFile().isDirectory()) {
                    setupListView(leftListView, true, itemsLeft.get(leftListView.getSelectionModel().getSelectedIndex()).getFile());
                }
            }
        });

        TableView<CommanderFile> rightListView = (TableView<CommanderFile>) root.lookup("#rightListView");
        rightListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (itemsRight.get(rightListView.getSelectionModel().getSelectedIndex()).getFile().isDirectory()) {
                    setupListView(rightListView, false, itemsRight.get(rightListView.getSelectionModel().getSelectedIndex()).getFile());
                }
            }
        });

        TableColumn<CommanderFile, String> nameColumnLeft = new TableColumn<>("Name");
        nameColumnLeft.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<CommanderFile, String> dateColumnLeft = new TableColumn<>("Date");
        dateColumnLeft.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<CommanderFile, String> nameColumnRight = new TableColumn<>("Name");
        nameColumnRight.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<CommanderFile, String> dateColumnRight = new TableColumn<>("Date");
        dateColumnRight.setCellValueFactory(new PropertyValueFactory<>("date"));

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

        File folder = new File("C:/");
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

    private void setupListView(TableView<CommanderFile> tableView, boolean isLeft, File file) {
        List<File> listOfFiles = new ArrayList<>(Arrays.asList(file.listFiles()));

        if (file.getParentFile() != null) {
            listOfFiles.add(0, file.getParentFile());
        }

        if (isLeft) {
            currentPathLeft = file.getAbsolutePath();
            itemsLeft.clear();
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    System.out.println("File " + listOfFile.getName());
                    itemsLeft.add(new CommanderFile(listOfFile)); //"\uD83D\uDDC4 " + listOfFile.getName());

                } else if (listOfFile.isDirectory()) {
                    System.out.println("Directory " + listOfFile.getName());
                    itemsLeft.add(new CommanderFile(listOfFile)); //"\uD83D\uDCC1 " + listOfFile.getName());
                }
            }
            if (file.getParentFile() != null) {
                itemsLeft.set(0, new CommanderFile(file.getParentFile(), "⬅ .."));
            }

            tableView.setItems(itemsLeft);
        } else {
            currentPathRight = file.getAbsolutePath();
            itemsRight.clear();
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    System.out.println("File " + listOfFile.getName());
                    itemsRight.add(new CommanderFile(listOfFile)); //"\uD83D\uDDC4 " + listOfFile.getName());

                } else if (listOfFile.isDirectory()) {
                    System.out.println("Directory " + listOfFile.getName());
                    itemsRight.add(new CommanderFile(listOfFile)); //"\uD83D\uDCC1 " + listOfFile.getName());
                }
            }
            if (file.getParentFile() != null) {
                itemsRight.set(0, new CommanderFile(file.getParentFile(), "⬅ .."));
            }
            tableView.setItems(itemsRight);
        }
        pathFieldLeft.setText(currentPathLeft);
        pathFieldRight.setText(currentPathRight);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
