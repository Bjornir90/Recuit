package com.scep.fxui;

import com.scep.fxui.model.Model;
import com.scep.fxui.model.StationRow;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class DetParamController {
    @FXML TableView<StationRow> table;
    @FXML TableColumn<StationRow, Float> cColumn;
    @FXML TableColumn<StationRow, Float> vColumn;
    @FXML TableColumn<StationRow, Float> wColumn;
    @FXML TableColumn<StationRow, Integer> kColumn;
    Model model;

    @FXML
    private void initialize() {

        cColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        vColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        wColumn.setCellFactory(TextFieldTableCell.forTableColumn(new FloatStringConverter()));
        kColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        cColumn.setOnEditCommit(e -> e.getRowValue().setC(e.getNewValue()));
        vColumn.setOnEditCommit(e -> e.getRowValue().setV(e.getNewValue()));
        wColumn.setOnEditCommit(e -> e.getRowValue().setW(e.getNewValue()));
        kColumn.setOnEditCommit(e -> e.getRowValue().setK(e.getNewValue()));
    }

    void setModel(Model model) {
        this.model = model;
        table.setItems(model.getRowList());
    }
}
