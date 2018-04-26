package com.huuthuong.view;

import com.huuthuong.controller.CustomGridViewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.example.Person;


public class Main extends Application {
    private ObservableList<Person> persons = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) throws Exception{
        generatePersons();

        primaryStage.setTitle("CustomGridView");
        primaryStage.setScene(new Scene(new CustomGridViewController("/mainview.fxml", persons).getRoot(), 600, 400));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void generatePersons() {
        for (int i=0; i < 100; i++) {
            persons.add(new Person("" + (i+1), "" + ((int) (Math.random() * 1000))));
        }
    }

}
