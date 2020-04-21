package simulation.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Region {
    private List<City> cities = new ArrayList<>();
    private Map<City, List<Stats>> allStats = new HashMap<>();
    private Virus virus;

    public void addCity(String name, int population, int saturation) {
        City city = new City(name, population, saturation);
        cities.add(city);
        allStats.put(city, new ArrayList<>());
    }

    public void addVirus(Virus virus) {
        this.virus = virus;
    }

    public Map<City, List<Stats>> countAllStats() {
        for (City city : cities) {
            allStats.get(city).add(city.countStats());
        }
        return allStats;
    }

    public void nextStep() {
        for (City city : cities) {
            city.nextStep();
        }
        activateFlights();
    }

    private void activateFlights() {
        List<List<Human>> flights = new ArrayList<>();
        List<Integer> flightsSizes = new ArrayList<>();
        int sumPopulation = 0;
        for (City city : cities) {
            flights.add(city.sendRandomPeople());
            sumPopulation += city.size();
            flightsSizes.add(flights.get(flights.size() - 1).size());
        }
        for (City city : cities) {
            double passCoeff = (double) city.size() / sumPopulation;
            for (int i = 0; i < flights.size(); i++) {
                List<Human> flight = flights.get(i);
                int receiveNumb = (int) (passCoeff * flightsSizes.get(i));  // high plane lethality:)
                city.receivePeople(flight.subList(flight.size() - receiveNumb, flight.size()));
                flights.set(i, flight.subList(0, flight.size() - receiveNumb));
            }
        }
    }

    public void startSimulation() {
        cities.get((int) (Math.random() * cities.size())).infectZeroPatient(virus);
    }

    public Virus getVirus() {
        return virus;
    }

    public List<City> getCities() {
        return cities;
    }

    public Map<City, List<Stats>> getReadyStats() {
        return allStats;
    }
}
