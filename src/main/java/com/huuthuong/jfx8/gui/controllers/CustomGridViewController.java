package com.huuthuong.jfx8.gui.controllers;

import com.huuthuong.jfx8.gui.helpers.CustomGridView;
import com.huuthuong.jfx8.models.Person;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import java.net.URL;


public class CustomGridViewController {
    public final static KeyCombination COMBINATION_LEFT = new KeyCodeCombination(KeyCode.LEFT);
    public final static KeyCombination COMBINATION_UP = new KeyCodeCombination(KeyCode.UP);
    public final static KeyCombination COMBINATION_DOWN = new KeyCodeCombination(KeyCode.DOWN);
    public final static KeyCombination COMBINATION_RIGHT = new KeyCodeCombination(KeyCode.RIGHT);

    private static final double CELL_HEIGHT = 60.0;
    private static final double CELL_WIDTH = 100.0;
    private static final double CELL_SPACING = 5.0;
    private static final double CONTROL_PADDING = 10.0;

    private Property<Person> selectedPerson = new SimpleObjectProperty<>();
    FilteredList<Person> filteredList;

    private Parent root;
    @FXML private TextField txSearch;
    @FXML private CustomGridView<Person> cgvPersons;


    public CustomGridViewController(String view, ObservableList<Person> persons) throws Exception {
        URL resource = this.getClass().getResource(view);
        if(resource == null)
            throw new NullPointerException(view);
        FXMLLoader loader = new FXMLLoader(resource);
        loader.setController(this);
        this.root = loader.load();

        initialize(persons);
    }

    private void initialize(ObservableList<Person> persons) {
        filteredList = new FilteredList<>(persons);

        txSearch.setOnKeyPressed(e -> {
            if (COMBINATION_DOWN.match(e) && !e.isShortcutDown())
                cgvPersons.moveNext(e, selectedPerson.getValue());
        });
        txSearch.textProperty().addListener(_TextChanged);

        cgvPersons.setUpdateSelection(this::updateSelection);
        cgvPersons.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cgvPersons.setPadding(new Insets(CONTROL_PADDING, CONTROL_PADDING, 0, CONTROL_PADDING));
        cgvPersons.setCellHeight(CELL_HEIGHT);
        cgvPersons.setCellWidth(CELL_WIDTH);
        cgvPersons.setHorizontalCellSpacing(CELL_SPACING);
        cgvPersons.setVerticalCellSpacing(CELL_SPACING);

        cgvPersons.setItems(filteredList);
        cgvPersons.setCellFactory(new Callback<GridView<Person>, GridCell<Person>>() {
            @Override
            public GridCell<Person> call(GridView<Person> param) {
                return new GridCell<Person>() {
                    private VBox vbCell;
                    private Label lblName;
                    private Label lblAge;
                    private Person p;

                    private void initialize() {
                        vbCell = new VBox();
                        vbCell.getStyleClass().add("item");
                        vbCell.setSpacing(5);
                        vbCell.setMinSize(50, 50);
                        vbCell.managedProperty().bind(vbCell.visibleProperty());
                        vbCell.setOnMouseClicked((mouseEvent) -> {
                            if (mouseEvent.isShortcutDown() || mouseEvent.getClickCount() != 1 || mouseEvent.getButton() != MouseButton.PRIMARY)
                                return;
                            updateSelection(p);
                        });
                        lblName = new Label();
                        lblName.setMinSize(50, 10);
                        VBox.setMargin(lblName, new Insets(5));
                        lblAge = new Label();
                        lblAge.setMinSize(50, 10);
                        VBox.setMargin(lblAge, new Insets(5));
                        vbCell.getChildren().addAll(lblName, lblAge);
                    }

                    private void setPerson(Person person) {
                        lblName.setText("");
                        lblAge.setText("");
                        vbCell.setVisible(false);
                        if (p != null)
                            p.selectedProperty().removeListener(_SelectionChanged);
                        if (person == null)
                            return;
                        p = person;
                        lblName.setText("Name: " + person.getName());
                        lblAge.setText("Age: " + person.getAge());
                        vbCell.setVisible(true);
                        p.selectedProperty().addListener(_SelectionChanged);
                        _SelectionChanged.changed(p.selectedProperty(), null, p.selectedProperty().getValue());
                    }

                    private ChangeListener<Boolean> _SelectionChanged = (observable, oldValue, newValue) -> {
                        setSelected(newValue);
                    };

                    public void setSelected(boolean selected) {
                        vbCell.getStyleClass().remove("item_selected");
                        if (selected)
                            vbCell.getStyleClass().add("item_selected");
                    }

                    @Override
                    protected void updateItem(Person item, boolean empty) {
                        if (vbCell == null)
                            initialize();
                        super.updateItem(item, empty);
                        if (empty || (item == null)) {
                            setGraphic(null);
                            setPerson(null);
                        } else {
                            setPerson(item);
                            setGraphic(vbCell);
                        }
                    }
                };
            }
        });

        cgvPersons.addEventFilter(KeyEvent.KEY_PRESSED, (keyEvent) -> {
            if (cgvPersons != null) {
                cgvPersons.moveNext(keyEvent, selectedPerson.getValue());
            }
        });
    }

    public Parent getRoot() {
        return root;
    }

    private void updateSelection(Person person) {
        Person selecting = selectedPerson.getValue();
        if (selecting != null && !selecting.equals(person))
            selecting.selectedProperty().setValue(false);
        selectedPerson.setValue(person);
        if (person != null)
            person.selectedProperty().setValue(true);
    }

    private ChangeListener<String> _TextChanged = (observable, oldValue, newValue) -> {
        updateSelection(null);
        if (newValue == null || newValue.isEmpty()) {
            filteredList.setPredicate(p -> true);
        } else {
            filteredList.setPredicate(p -> p.match(newValue));
        }
    };

}
