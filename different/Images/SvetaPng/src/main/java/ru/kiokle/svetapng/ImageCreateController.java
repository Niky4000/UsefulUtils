package ru.kiokle.svetapng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import static ru.kiokle.marykaylib.CommonUtil.APPLICATION_CONFIGS;
import static ru.kiokle.marykaylib.CommonUtil.getConfigs;
import static ru.kiokle.marykaylib.CommonUtil.getPathToSaveFolder;
import static ru.kiokle.marykaylib.CommonUtil.s;

/**
 * @author NAnishhenko
 */
public class ImageCreateController implements Initializable {

    @FXML
    CheckBox oneFileCheckBox;
    @FXML
    CheckBox manyFilesCheckBox;
    @FXML
    TextField pathToOutImages;
    @FXML
    ComboBox pathToImageTemplate;
    @FXML
    TextField fontName;
    @FXML
    TextField columnsCount;
    @FXML
    TextField rowsCount;
    @FXML
    TableView<ImageLabelTableBean> configTable;

    private static File saveFolder = getPathToSaveFolder(ImageCreateController.class);

    private static boolean isWindowsOS = isWindows();
    private static final String pathToSelf = saveFolder.getParent();

    public static String getPathToSelf() {
        return pathToSelf;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Properties configs = getConfigs(saveFolder);
        handlePathTextField(pathToOutImages, "Images" + s + "image.png", () -> configs.getProperty("pathToOutImages"));
        handleComboBox(pathToImageTemplate, IMAGES_TEMPLATES, () -> configs.getProperty("pathToImageTemplate"));
        handleDigitTextField(columnsCount, () -> configs.getProperty("columnsCount"));
        handleDigitTextField(rowsCount, () -> configs.getProperty("rowsCount"));
        handleFontName(() -> configs.getProperty("fontName"));
        checkCheckBoxes(configs);
        createConfigTable(configs);
    }
    private static final String IMAGES_TEMPLATES = "ImagesTemplates";

    private void createConfigTable(Properties configs) {
        String property = configs.getProperty("tableContent");
        List<ImageLabelTableBean> imageLabelTableBeanList;
        if (property != null) {
            imageLabelTableBeanList = Arrays.asList(property.split(SPLITTER)).stream().map(obj -> ImageLabelTableBean.fromString(obj)).collect(Collectors.toList());
        } else {
            imageLabelTableBeanList = Arrays.asList(DEFAULT_ELEMENT.get());
        }

        configTable.getColumns().clear();

        Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn p) {
                return new EditingCell();
            }
        };

        //        TableColumn textColumn = new TableColumn("text");
//        textColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
//
//
//        textColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ImageLabelTableBean, String>>() {
//            @Override
//            public void handle(TableColumn.CellEditEvent<ImageLabelTableBean, String> t) {
//                ((ImageLabelTableBean) t.getTableView().getItems().get(t.getTablePosition().getRow())).setText(t.getNewValue());
//            }
//        });
//        textColumn.setOnEditCancel(new EventHandler<TableColumn.CellEditEvent<ImageLabelTableBean, String>>() {
//            @Override
//            public void handle(TableColumn.CellEditEvent<ImageLabelTableBean, String> t) {
//                ((ImageLabelTableBean) t.getTableView().getItems().get(t.getTablePosition().getRow())).setText(t.getNewValue());
//            }
//        });
//        textColumn.setCellFactory(cellFactory);
//        TableColumn textColumn = createColumn(configTable);
//        textColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        TableColumn<ImageLabelTableBean, String> textColumn = createEditableAreaColumn("Текст наклейки", obj -> obj.getTextProperty());
        TableColumn<ImageLabelTableBean, String> textSizeColumn = createEditableColumn("Размер текста", obj -> obj.getTextSizeProperty());
        TableColumn<ImageLabelTableBean, String> valignColumn = createEditableColumn("Вертикальное смещение", obj -> obj.getValignProperty());
        TableColumn<ImageLabelTableBean, String> countColumn = createEditableColumn("Количество", obj -> obj.getCountProperty());
//        TableColumn checkBoxColumn = createCheckBoxColumn("Hello", null);

        textColumn.prefWidthProperty().bind(configTable.widthProperty().multiply(0.6));
//        checkBoxColumn.prefWidthProperty().bind(configTable.widthProperty().multiply(0.1));
        textSizeColumn.prefWidthProperty().bind(configTable.widthProperty().multiply(0.2));
        valignColumn.prefWidthProperty().bind(configTable.widthProperty().multiply(0.1));
        countColumn.prefWidthProperty().bind(configTable.widthProperty().multiply(0.1));

//        configTable.getColumns().addAll(textColumn, textSizeColumn, valignColumn, countColumn);
        configTable.getColumns().addAll(textColumn, textSizeColumn, valignColumn, countColumn);
        ObservableList<ImageLabelTableBean> data = FXCollections.observableArrayList(imageLabelTableBeanList);
        configTable.setItems(data);
        setTableEditable(configTable);
    }
    private static final Supplier<ImageLabelTableBean> DEFAULT_ELEMENT = () -> new ImageLabelTableBean("Hello", "400", "0", "1");

    private <T> TableColumn createCheckBoxColumn(final String columnName, Class<T> objClass) {
        TableColumn actionCol = new TableColumn(columnName);
        actionCol.setCellValueFactory(new PropertyValueFactory<>("checked"));
        Callback<TableColumn<T, String>, TableCell<T, String>> cellFactory = new Callback<TableColumn<T, String>, TableCell<T, String>>() {
            @Override
            public TableCell<T, String> call(TableColumn<T, String> p) {
                final TableCell<T, String> cell = new TableCell<T, String>() {

                    final TextArea textArea = new TextArea();

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setGraphic(textArea);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        actionCol.setCellFactory(cellFactory);
        return actionCol;
    }

    private TableColumn<ImageLabelTableBean, String> createEditableAreaColumn(String header, Function<ImageLabelTableBean, SimpleStringProperty> text) {
        final TableColumn<ImageLabelTableBean, String> textColumn = new TableColumn<ImageLabelTableBean, String>(header);
        textColumn.setCellValueFactory(cellDataFeatures -> text.apply(cellDataFeatures.getValue()));
        textColumn.setCellFactory(cellDataFeatures -> new StringAreaTableCell());
        return textColumn;
    }

    private TableColumn<ImageLabelTableBean, String> createEditableColumn(String header, Function<ImageLabelTableBean, SimpleStringProperty> text) {
        final TableColumn<ImageLabelTableBean, String> textColumn = new TableColumn<ImageLabelTableBean, String>(header);
        textColumn.setCellValueFactory(cellDataFeatures -> text.apply(cellDataFeatures.getValue()));
        textColumn.setCellFactory(cellDataFeatures -> new StringTableCell());
        return textColumn;
    }

    private TableColumn<ImageLabelTableBean, String> createColumn(TableView<ImageLabelTableBean> table) {
        TableColumn<ImageLabelTableBean, String> column = new TableColumn<>("text");
        column.setCellValueFactory(obj -> {
            SimpleObjectProperty< String> property = new SimpleObjectProperty<>();
            property.setValue(obj.getValue().getText());
            return property;
        });
//        table.getColumns().add(2, column);
        setupColumn(configTable, column);
        return column;
    }

    private void setupColumn(TableView<ImageLabelTableBean> table, TableColumn<ImageLabelTableBean, String> column) {
        // formats the display value to display dates in the form of dd/MM/yyyy
//		column.setCellFactory(EditCell.<ImageLabelTableBean, String>forTableColumn(
//						new MyDateStringConverter(DATE_PATTERN)));
        // updates the dateOfBirth field on the PersonTableData object to the
        // committed value
        column.setOnEditCommit(event -> {
            final String value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            ((ImageLabelTableBean) event.getTableView().getItems().get(event.getTablePosition().getRow())).setText(value);
            table.refresh();
        });
    }

    private void setTableEditable(TableView<ImageLabelTableBean> table) {
        table.setEditable(true);
        // allows the individual cells to be selected
        table.getSelectionModel().cellSelectionEnabledProperty().set(true);
        // when character or numbers pressed it will start edit in editable
        // fields
        table.setOnKeyPressed(event -> {
            if (event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
                editFocusedCell(table);
            } else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.TAB) {
                table.getSelectionModel().selectNext();
                event.consume();
            } else if (event.getCode() == KeyCode.LEFT) {
                // work around due to
                // TableView.getSelectionModel().selectPrevious() due to a bug
                // stopping it from working on
                // the first column in the last row of the table
                selectPrevious(table);
                event.consume();
            }
        });
    }

    @SuppressWarnings("unchecked")

    private void editFocusedCell(TableView<ImageLabelTableBean> table) {
        final TablePosition< ImageLabelTableBean, ?> focusedCell = table.focusModelProperty().get().focusedCellProperty().get();
        table.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    @SuppressWarnings("unchecked")

    private void selectPrevious(TableView<ImageLabelTableBean> table) {
        if (table.getSelectionModel().isCellSelectionEnabled()) {
            // in cell selection mode, we have to wrap around, going from
            // right-to-left, and then wrapping to the end of the previous line
            TablePosition< ImageLabelTableBean, ?> pos = table.getFocusModel().getFocusedCell();
            if (pos.getColumn() - 1 >= 0) {
                // go to previous row
                table.getSelectionModel().select(pos.getRow(), getTableColumn(table, pos.getTableColumn(), -1));
            } else if (pos.getRow() < table.getItems().size()) {
                // wrap to end of previous row
                table.getSelectionModel().select(pos.getRow() - 1, table.getVisibleLeafColumn(table.getVisibleLeafColumns().size() - 1));
            }
        } else {
            int focusIndex = table.getFocusModel().getFocusedIndex();
            if (focusIndex == -1) {
                table.getSelectionModel().select(table.getItems().size() - 1);
            } else if (focusIndex > 0) {
                table.getSelectionModel().select(focusIndex - 1);
            }
        }
    }

    private TableColumn< ImageLabelTableBean, ?> getTableColumn(TableView<ImageLabelTableBean> table, final TableColumn< ImageLabelTableBean, ?> column, int offset) {
        int columnIndex = table.getVisibleLeafIndex(column);
        int newColumnIndex = columnIndex + offset;
        return table.getVisibleLeafColumn(newColumnIndex);
    }

    private void checkCheckBoxes(Properties configs) {
        if (configs != null && configs.get("oneFileCheckBox") != null && configs.get("manyFilesCheckBox") != null && configs.get("oneFileCheckBox").equals("1") && configs.get("manyFilesCheckBox").equals("0")) {
            oneFileCheckBox.setSelected(true);
            manyFilesCheckBox.setSelected(false);
        } else if (configs != null && configs.get("oneFileCheckBox") != null && configs.get("manyFilesCheckBox") != null && configs.get("oneFileCheckBox").equals("0") && configs.get("manyFilesCheckBox").equals("1")) {
            oneFileCheckBox.setSelected(false);
            manyFilesCheckBox.setSelected(true);
        } else if ((!oneFileCheckBox.isSelected() && !manyFilesCheckBox.isSelected()) || oneFileCheckBox.isSelected() && manyFilesCheckBox.isSelected()) {
            oneFileCheckBox.setSelected(true);
            manyFilesCheckBox.setSelected(false);
        }
    }

    private void handleFontName(Supplier<String> stringFromConfigs) {
        if (stringFromConfigs.get() != null) {
            fontName.setText(stringFromConfigs.get());
        } else if (fontName.getText().length() == 0 && isWindowsOS) {
            fontName.setText("Times New Roman");
        } else if (fontName.getText().length() == 0 && !isWindowsOS) {
            fontName.setText("Liberation Serif");
        }
    }

    private Integer handleDigitTextField(TextField textField, Supplier<String> stringFromConfigs) {
        if (stringFromConfigs != null) {
            try {
                Integer intValue = Integer.valueOf(stringFromConfigs.get());
                textField.setText(intValue.toString());
                return intValue;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (textField.getText().length() == 0) {
            textField.setText("1");
            return 1;
        }
        try {
            return Integer.valueOf(textField.getText());
        } catch (Exception e) {
            textField.setText("1");
            return 1;
        }
    }

    private void handlePathTextField(TextField textField, String imageName, Supplier<String> stringFromConfigs) {
        if (stringFromConfigs.get() != null) {
            textField.setText(stringFromConfigs.get());
        } else if (textField.getText().length() == 0 || !new File(textField.getText()).exists() || !textField.getText().endsWith(".png")) {
            textField.setText(new File(pathToSelf + s + imageName).getAbsolutePath());
        }
    }

    private void handleComboBox(ComboBox comboBox, String imageName, Supplier<String> stringFromConfigs) {
        List<String> fileNames = Arrays.stream(new File(pathToSelf + s + imageName).listFiles()).filter(file -> file.getName().endsWith(".png")).map(file -> file.getName()).collect(Collectors.toList());
        ObservableList<String> options = FXCollections.observableArrayList(fileNames);
        comboBox.setItems(options);
        if (stringFromConfigs.get() != null && new File(stringFromConfigs.get()).exists()) {
            String name = new File(stringFromConfigs.get()).getName();
            comboBox.getSelectionModel().select(name);
        } else if (!fileNames.isEmpty()) {
            comboBox.getSelectionModel().selectFirst();
        }
    }

    private File handlePathFromTextField(TextField textField) {
        File file = new File(textField.getText());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    private File handlePathFromComboBox(ComboBox comboBox, String imageName) {
        File file = new File(pathToSelf + s + imageName + s + (String) comboBox.getValue());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        return file;
    }

    @FXML
    public void addTableRowAction(ActionEvent event) {
        configTable.getItems().add(DEFAULT_ELEMENT.get());
    }

    @FXML
    public void deleteTableRowAction(ActionEvent event) {
        int size = configTable.getItems().size();
        if (size > 1) {
            configTable.getItems().remove(size - 1);
        }
    }

    @FXML
    public void createImageAction(ActionEvent event) {
        List<SvetaStart.ImageLabel> imageLabelList = configTable.getItems().stream().map(obj -> new SvetaStart.ImageLabel(obj.getText(), Integer.valueOf(obj.getTextSize()), Integer.valueOf(obj.getValign()), Integer.valueOf(obj.getCount()))).collect(Collectors.toList());
        File outputImagesPath = handlePathFromTextField(pathToOutImages);
        File templateImagePath = handlePathFromComboBox(pathToImageTemplate, IMAGES_TEMPLATES);
        Integer columnsCountValue = handleDigitTextField(columnsCount, null);
        Integer rowsCountValue = handleDigitTextField(rowsCount, null);
        String conf = oneFileCheckBox.isSelected() ? "-conf" : "-confLine";
        try {
//            String[] args = new String[]{"-t", outputImagesPath.getAbsolutePath(), "-o", templateImagePath.getAbsolutePath(), conf, saveFolder.getAbsolutePath() + s + "config.txt", "-columns", columnsCountValue.toString(), "-rows", rowsCountValue.toString()};
//            SvetaStart.main(args);
            SvetaStart.createImage(templateImagePath, oneFileCheckBox.isSelected(), imageLabelList, fontName.getText(), outputImagesPath, columnsCountValue, rowsCountValue);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Создание картинок успешно завершено!");
            alert.setHeaderText("Создание картинок успешно завершено!");
            alert.setContentText("Картинки лежат в каталоге:\n" + outputImagesPath.getAbsolutePath() + "!");
            alert.setResizable(true);
            alert.showAndWait();
        } catch (Exception ex) {
            Logger.getLogger(ImageCreateController.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Произошла ошибка во время создания картинок!");
            alert.setHeaderText("Произошла ошибка во время создания картинок!");
            alert.setContentText("Произошла ошибка во время создания картинок!");
            alert.showAndWait();
        }
        System.out.println("Hello from button!");

    }

    @FXML
    public void oneFileCheckBoxAction(ActionEvent event) {
        System.out.println("oneFileCheckBox " + (oneFileCheckBox.isSelected() ? "selected" : "non selected") + "!");
        changeCheckBoxState(oneFileCheckBox.isSelected(), manyFilesCheckBox);
    }

    @FXML
    public void manyFilesCheckBoxAction(ActionEvent event) {
        System.out.println("manyFilesCheckBoxAction " + (oneFileCheckBox.isSelected() ? "selected" : "non selected") + "!");
        changeCheckBoxState(manyFilesCheckBox.isSelected(), oneFileCheckBox);
    }

    private void changeCheckBoxState(boolean selected, CheckBox checkBox) {
        if (selected) {
            checkBox.setSelected(false);
        } else {
            checkBox.setSelected(true);
        }
    }

    protected static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("win") != -1;
    }

    private void grapConfigs() throws FileNotFoundException, IOException {
        Properties properties = getConfigs(saveFolder);
        properties.setProperty("oneFileCheckBox", oneFileCheckBox.isSelected() ? "1" : "0");
        properties.setProperty("manyFilesCheckBox", manyFilesCheckBox.isSelected() ? "1" : "0");
        properties.setProperty("pathToOutImages", pathToOutImages.getText());
        properties.setProperty("pathToImageTemplate", handlePathFromComboBox(pathToImageTemplate, IMAGES_TEMPLATES).getAbsolutePath());
        properties.setProperty("fontName", fontName.getText());
        properties.setProperty("columnsCount", columnsCount.getText());
        properties.setProperty("rowsCount", rowsCount.getText());
        Optional<String> tableContent = configTable.getItems().stream().map(obj -> obj.toString()).reduce((obj1, obj2) -> obj1 + SPLITTER + obj2);
        if (tableContent.isPresent()) {
            properties.setProperty("tableContent", tableContent.get());
        }
        properties.store(new FileOutputStream(new File(saveFolder + s + APPLICATION_CONFIGS)), "configs");
    }
    private static final String SPLITTER = "&";

    public void shutdown() {
        try {
            grapConfigs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // cleanup code here...
        System.out.println("Stop!");
//        action();
        // note that typically (i.e. if Platform.isImplicitExit() is true, which is the default)
        // closing the last open window will invoke Platform.exit() anyway
        Platform.exit();
    }
}
