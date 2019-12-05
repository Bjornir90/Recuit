package com.scep.fxui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class MainController {
    @FXML Button detParamBtn;
    @FXML Text dataStatus;
    @FXML TextField recItIn;
    @FXML TextField recItOut;
    @FXML ComboBox resMethod;
    @FXML Button resolve;
    @FXML TableView results;
    @FXML Hyperlink export;

    @FXML
    private void initialize() {
    }
}
