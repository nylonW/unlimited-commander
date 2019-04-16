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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    private List<File> leftListOfFile;
    private List<File> rightListOfFile;

    ObservableList<CommanderFile> itemsLeft = FXCollections.observableArrayList();
    ObservableList<CommanderFile> itemsRight = FXCollections.observableArrayList();

    private String currentPathLeft;
    private String currentPathRight;

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

        leftListView.getColumns().addAll(nameColumnLeft, dateColumnLeft);
        rightListView.getColumns().addAll(nameColumnRight, dateColumnRight);

        File folder = new File("C:/");

        setupListView(leftListView, true, folder);
        setupListView(rightListView, false, folder);
    }

    private void setupListView(TableView<CommanderFile> tableView, boolean isLeft, File file) {
        List<File> listOfFiles = new ArrayList<>(Arrays.asList(file.listFiles()));

        if (file.getParentFile() != null) {
            listOfFiles.add(0, file.getParentFile());
        }

        if (listOfFiles != null) {
            if (isLeft) {
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

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
