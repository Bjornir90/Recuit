package com.scep.fxui;

import com.scep.CplexVLS;
import com.scep.Solution;
import com.scep.fxui.model.Model;
import com.scep.fxui.model.StationRow;
import ilog.concert.IloException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

enum ResMethod {
    RECUIT("Recuit"),
    CPLEX("CPLEX");

    private final String text;
    ResMethod(final String text) {
        this.text = text;
    }
    @Override
    public String toString() {
        return text;
    }
}

public class MainController {
    @FXML Button detParamBtn;
    @FXML Text dataStatus;
    @FXML TextField scenarios;
    @FXML ComboBox<ResMethod> resMethod;
    @FXML TextField recItIn;
    @FXML TextField recItOut;
    @FXML Button resolveBtn;
    @FXML TableView<StationRow> results;
    @FXML Hyperlink export;

    Stage primaryStage;
    private Model model;
    private Stage detParamStage;
    DetParamController detParamController;


    @FXML
    private void initialize() {
        new Thread(() -> {
            model = new Model();
            dataStatus.setText("Données chargées");
            if(detParamController != null)
                detParamController.setModel(model);
            detParamBtn.setDisable(false);
        }).start();

        resMethod.getItems().addAll(ResMethod.values());
        resMethod.addEventHandler(ActionEvent.ACTION, e -> {
            boolean loopFields = resMethod.getValue() == ResMethod.RECUIT;
            recItIn.setDisable(!loopFields);
            recItOut.setDisable(!loopFields);
            resolveBtn.setDisable(false);
        });

        resolveBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> solve());
        detParamBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> detParamStage.show());
    }

    void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        try {
            initDetParamDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDetParamDialog() throws IOException {
        detParamStage = new Stage();
        detParamStage.initModality(Modality.APPLICATION_MODAL);
        detParamStage.initOwner(primaryStage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/det-param.fxml"));
        TableView root = loader.load();
        detParamController = loader.getController();
        Scene scene = new Scene(root);
        detParamStage.setScene(scene);
    }

    private void solve() {
        resolveBtn.setDisable(true);

        new Thread(() -> {
            switch(resMethod.getValue()) {
                case RECUIT:
                    break;
                case CPLEX:
                    try {
                        CplexVLS cplex = new CplexVLS();
                        cplex.buildProblem(
                            model.getDataService().getDemand(),
                            model.getC(),
                            model.getV(),
                            model.getW(),
                            model.getK(),
                            Integer.parseInt(scenarios.getCharacters().toString())
                        );
                        Solution solution = cplex.compute();
                        model.setX(solution.dVars);
                    } catch (IloException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    throw new RuntimeException("No solver selected.");
            }

            if(results.getItems() == model.getRowList())
                results.refresh();
            else
                results.setItems(model.getRowList());

            resolveBtn.setDisable(false);
        }).start();
    }
}
