package simulation.UI;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import simulation.utils.City;
import simulation.utils.Region;
import simulation.utils.Stats;
import simulation.utils.Virus;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StagesFactory {
    private final String imgPath = getAssetsPath() + "Logo.png";
    private final String cityPath = getAssetsPath() + "Crimea.csv";
    private final String virusPath = getAssetsPath() + "Virus.csv";
    private final String statsPath = getAssetsPath() + "Stats.csv";
    private Image icon;
    private int currentStartPos;
    private List<TextField> cityNames = new ArrayList<>();
    private List<TextField> populations = new ArrayList<>();
    private List<TextField> saturations = new ArrayList<>();

    private Map<City, PieChart> charts = new HashMap<>();
    private Map<City, LineChart> graphs = new HashMap<>();
    private LineChart<Number, Number> regionGraph;
    private int weekNumber = 0;


    private String getAssetsPath() {
        return "D:\\Java\\Infection\\src\\simulation\\assets\\";
    }

    private void addCityToTheGrid(GridPane grid, int startPos, String name, String population, String saturation) {
        cityNames.add(new TextField(name));
        populations.add(new TextField(population));
        saturations.add(new TextField(saturation));
        grid.add(cityNames.get(cityNames.size() - 1), 0, startPos);
        grid.add(populations.get(populations.size() - 1), 1, startPos);
        grid.add(saturations.get(saturations.size() - 1), 2, startPos);
    }

    public Stage getStartStage(Region region) {
        Stage stage = new Stage();
        GridPane gridPane = new GridPane();

        Text mVirus = new Text("Virus");
        Text mR0 = new Text("R0 (contagiousness)");
        Text mLethality = new Text("Lethality (%)");
        mVirus.setStyle("-fx-font: normal bold 15px 'serif' ");
        mR0.setStyle("-fx-font: normal bold 15px 'serif' ");
        mLethality.setStyle("-fx-font: normal bold 15px 'serif' ");
        gridPane.add(mVirus, 0, currentStartPos);
        gridPane.add(mR0, 1, currentStartPos);
        gridPane.add(mLethality, 2, currentStartPos);
        currentStartPos++;

        TextField vName = new TextField();
        TextField vR0 = new TextField();
        TextField vLethality = new TextField();
        gridPane.add(vName, 0, currentStartPos);
        gridPane.add(vR0, 1, currentStartPos);
        gridPane.add(vLethality, 2, currentStartPos);
        currentStartPos++;


        Text mCity = new Text("City");
        Text mPopulation = new Text("Population (H)");
        Text mSaturation = new Text("Saturation (H/km^2)");
        mCity.setStyle("-fx-font: normal bold 15px 'serif' ");
        mPopulation.setStyle("-fx-font: normal bold 15px 'serif' ");
        mSaturation.setStyle("-fx-font: normal bold 15px 'serif' ");
        gridPane.add(mCity, 0, currentStartPos);
        gridPane.add(mPopulation, 1, currentStartPos);
        gridPane.add(mSaturation, 2, currentStartPos);
        try {
            File file = new File(imgPath);
            icon = new Image(file.toURI().toString());
            stage.getIcons().add(icon);
            BufferedReader br = new BufferedReader(new FileReader(cityPath));
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                String[] country = line.split(",");
                addCityToTheGrid(gridPane, ++currentStartPos, country[0], country[1], country[2]);
            }
            br = new BufferedReader(new FileReader(virusPath));
            br.readLine();
            String[] country = br.readLine().split(",");
            vName.setText(country[0]);
            vR0.setText(country[1]);
            vLethality.setText(country[2]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Button start = new Button("Start");
        start.setAlignment(Pos.TOP_CENTER);
        VBox bStart = new VBox(start);
        bStart.setAlignment(Pos.CENTER);
        start.setOnAction(event -> {
            for (int i = 0; i < cityNames.size(); i++) {
                if (!cityNames.get(i).getCharacters().toString().equals("")) {
                    region.addCity(cityNames.get(i).getCharacters().toString(),
                            Integer.parseInt(populations.get(i).getCharacters().toString()),
                            Integer.parseInt(saturations.get(i).getCharacters().toString()));
                }
            }
            region.addVirus(new Virus(vName.getCharacters().toString(),
                    Double.parseDouble(vR0.getCharacters().toString()),
                    Double.parseDouble(vLethality.getCharacters().toString()) / 100));
            stage.close();
            getSimulationStage(region).show();
        });

        gridPane.add(bStart, 1, ++currentStartPos);
        start.setStyle("-fx-background-color: #4e8b4b; -fx-textfill: white;");

        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setStyle("-fx-background-color: #acbff5;");

        Scene scene = new Scene(gridPane);
        stage.setTitle("Infection");
        stage.setScene(scene);
        return stage;
    }

    private Stage getSimulationStage(Region region) {
        Stage stage = new Stage();
        stage.getIcons().add(icon);
        stage.setTitle(region.getVirus().getName());

        GridPane gridPane = new GridPane();
        Scene scene = new Scene(gridPane);
        stage.setScene(scene);

        region.startSimulation();
        Map<City, List<Stats>> allStats = region.countAllStats();
        weekNumber++;
        int vertIdx = 0;
        int firstSumInfected = 0;
        int firstSumSusceptible = 0;
        int firstSumImmune = 0;
        for (City city : region.getCities()) {
            PieChart pieChart = new PieChart();
            pieChart.setMaxHeight(1);
            pieChart.setMaxWidth(1);
            pieChart.setPrefSize(1, 1);
            pieChart.setLabelsVisible(false);
            pieChart.setTitle(city.getName());
            Stats stats = allStats.get(city).get(allStats.get(city).size() - 1);
            firstSumInfected += stats.infected;
            firstSumSusceptible += stats.susceptible;
            firstSumImmune += stats.healthy - stats.susceptible;
            pieChart.getData().add(new PieChart.Data("Infected", stats.infected));
            pieChart.getData().add(new PieChart.Data("Susceptible", stats.susceptible));
            pieChart.getData().add(new PieChart.Data("Immune", stats.healthy - stats.susceptible));
            charts.put(city, pieChart);
            gridPane.add(pieChart, vertIdx / 4, vertIdx % 4);
            vertIdx++;
        }
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Weeks");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("People");
        regionGraph = new LineChart<>(xAxis, yAxis);

        XYChart.Series<Number, Number> infected = new XYChart.Series<>();
        infected.setName("Infected");
        infected.getData().add(new XYChart.Data<>(1, firstSumInfected));
        XYChart.Series<Number, Number> susceptible = new XYChart.Series<>();
        susceptible.setName("susceptible");
        susceptible.getData().add(new XYChart.Data<>(1, firstSumSusceptible));
        XYChart.Series<Number, Number> immune = new XYChart.Series<>();
        immune.setName("Immune");
        immune.getData().add(new XYChart.Data<>(1, firstSumImmune));
        regionGraph.getData().addAll(infected, susceptible, immune);
        Button nextStep = new Button("   Next Step   ");
        Button skip = new Button(" Skip 50 steps ");
        Button save = new Button("Save statistics");
        Button restart = new Button("    Restart    ");
        Button exit = new Button("      Exit     ");

        nextStep.setOnAction(event -> {
            weekNumber++;
            region.nextStep();
            Map<City, List<Stats>> newAllStats = region.countAllStats();
            int sumInfected = 0;
            int sumSusceptible = 0;
            int sumImmune = 0;
            for (City city : region.getCities()) {
                Stats stats = newAllStats.get(city).get(newAllStats.get(city).size() - 1);
                PieChart chart = charts.get(city);
                sumInfected += stats.infected;
                sumSusceptible += stats.susceptible;
                sumImmune += stats.healthy - stats.susceptible;
                chart.getData().get(0).setPieValue(stats.infected);
                chart.getData().get(1).setPieValue(stats.susceptible);
                chart.getData().get(2).setPieValue(stats.healthy - stats.susceptible);
            }
            regionGraph.getData().get(0).getData().add(new XYChart.Data<>(weekNumber, sumInfected));
            regionGraph.getData().get(1).getData().add(new XYChart.Data<>(weekNumber, sumSusceptible));
            regionGraph.getData().get(2).getData().add(new XYChart.Data<>(weekNumber, sumImmune));
        });

        skip.setOnAction(event -> new Thread(() -> {
            for (int i = 0; i < 50; i++) {
                Platform.runLater(nextStep::fire);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start());

        save.setOnAction(event -> saveStatistics(region));

        restart.setOnAction(event -> {
            stage.close();
            new StagesFactory().getStartStage(new Region()).show();
        });

        exit.setOnAction(event -> stage.close());

        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(nextStep, 4, 3);
        gridPane.add(skip, 5, 3);
        gridPane.add(save, 6, 3);
        gridPane.add(restart, 7, 3);
        gridPane.add(exit, 8, 3);
        gridPane.add(regionGraph, 4, 0, 5, 3);
        nextStep.setStyle("-fx-background-color: #4e8b4b; -fx-textfill: white;");
        skip.setStyle("-fx-background-color: #4e8b4b; -fx-textfill: white;");
        save.setStyle("-fx-background-color: #4e8b4b; -fx-textfill: white;");
        restart.setStyle("-fx-background-color: #8b8200; -fx-textfill: white;");
        exit.setStyle("-fx-background-color: #8b3400; -fx-textfill: white;");
        
        return stage;
    }

    private void saveStatistics(Region region) {
        try {
            FileWriter csvWriter = new FileWriter(statsPath);
            Map<City, List<Stats>> allStats = region.getReadyStats();
            List<City> cities = region.getCities();
            for (int i = 0; i < allStats.get(cities.get(0)).size(); i++) {
                csvWriter.append(",Week ").append(String.valueOf(i)).append("\n");
                csvWriter.append("City,Budget,Population,Infected,Susceptible,Immune,Healthy,Vaccinated(step),Dead(step)\n");
                for (City city : cities) {
                    Stats stats = allStats.get(city).get(i);
                    csvWriter.append(city.getName());
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf((int) stats.budget));
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(stats.population));
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(stats.infected));
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(stats.susceptible));
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(stats.healthy - stats.susceptible));
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(stats.healthy));
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(stats.vaccinated));
                    csvWriter.append(",");
                    csvWriter.append(String.valueOf(stats.dead));
                    csvWriter.append("\n");
                }
                csvWriter.append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
