package com.scep.fxui;

import com.scep.fxui.model.Model;
import com.scep.fxui.model.StationRow;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class DetParamController {
    @FXML
    TableView<StationRow> table;
    Model model;

    @FXML
    private void initialize() {
    }

    void setModel(Model model) {
        this.model = model;
        table.setItems(model.getRowList());
    }
}
