module com.huuthuong.view {
    requires model.example;
    requires helper.control;
    requires javafx.fxml;
    requires javafx.controls;
    requires controlsfx;
    opens com.huuthuong.controller;
    exports com.huuthuong.view;
}