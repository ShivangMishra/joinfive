module com.example.joinfive {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.joinfive to javafx.fxml;
    exports com.example.joinfive;
    exports com.example.joinfive.model;
    opens com.example.joinfive.model to javafx.fxml;
    exports com.example.joinfive.model.grid;
    opens com.example.joinfive.model.grid to javafx.fxml;
    exports com.example.joinfive.model.ai;
    opens com.example.joinfive.model.ai to javafx.fxml;
    exports com.example.joinfive.controller;
    opens com.example.joinfive.controller to javafx.fxml;
    exports com.example.joinfive.view;
    opens com.example.joinfive.view to javafx.fxml;
    exports com.example.joinfive.util;
    opens com.example.joinfive.util to javafx.fxml;
    exports com.example.joinfive.model.ai.nmcs;
    opens com.example.joinfive.model.ai.nmcs to javafx.fxml;
    exports com.example.joinfive.model.ai.nrpa;
    opens com.example.joinfive.model.ai.nrpa to javafx.fxml;
}