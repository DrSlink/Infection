package simulation;

import javafx.application.Application;

import javafx.stage.Stage;
import simulation.UI.StagesFactory;
import simulation.utils.Region;


public class Main extends Application {
    Region region = new Region();
    private StagesFactory stagesFactory = new StagesFactory();

    @Override
    public void start(Stage stage) {
        stage = stagesFactory.getStartStage(region);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
