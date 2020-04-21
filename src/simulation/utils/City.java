package simulation.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class City {
    private final static double TAXES = 0.05;
    private final static double VACCINES_RATIO = 0.001;
    private final static double FLYING_RATIO = 0.1;

    private String name;
    private List<Human> humans = new ArrayList<>();
    private int saturation;
    private double budget = 0;

    private Random randGenerator = new Random();


    City(String name, int size, int saturation) {
        this.name = name;
        this.saturation = saturation;
        for (int i = 0; i < size; i++) {
            humans.add(new Human());
        }
    }

    public String getName() {
        return name;
    }

    void infectZeroPatient(Virus virus) {
        humans.get((int) (randGenerator.nextDouble() * humans.size())).infect(virus);
    }

    void nextStep() {
        for (int humanID = 0; humanID < humans.size(); humanID++) {
            Human human = humans.get(humanID);
            if (human.isInfected() && human.getAntigenIterations() > 0) {
                budget--;
                Virus virus = human.getInfection();
                int bound = (int) (virus.getR0() * randGenerator.nextDouble() * 2);
                for (int j = 0; j < bound; j++) {
                    int index = Math.min(Math.max(0, (int) ((randGenerator.nextDouble() - 0.5) * saturation) + humanID), humans.size() - 1);
                    humans.get(index).infect(virus); // count real R0
                }
            } else {
                budget += TAXES;
            }
        }
    }

    Stats countStats() {
        Stats stats = new Stats();
        int numbVaccines = (int) (budget * VACCINES_RATIO);
        List<Integer> dead = new ArrayList<>();
        for (int humanID = 0; humanID < humans.size(); humanID++) {
            Human human = humans.get(humanID);
            if (human.isInfected()) {
                if (!human.tryToImmunize(randGenerator)) {
                    if (human.getInfection().getLethality() > randGenerator.nextDouble()) {
                        dead.add(humanID);
                    } else {
                        stats.infected++;
                    }
                }
            } else if (human.isSusceptible()) {
                stats.susceptible++;
                if (human.isVaccinated()) {
                    human.tryToImmunize(randGenerator);
                } else if (numbVaccines > 0) {
                    human.vaccinate();
                    stats.vaccinated++;
                    numbVaccines--;
                    budget--;
                }
            }
        }
        sendPeople(dead);
        stats.budget = budget;
        stats.population = humans.size();
        stats.healthy = stats.population - stats.infected;
        stats.dead = dead.size();
        return stats;
    }

    private List<Human> sendPeople(List<Integer> idxes) {
        List<Human> aircraft = new ArrayList<>(idxes.size());
        for (int firstIdx = 0, secondIdx = 0, i = 0; secondIdx < humans.size(); secondIdx++) {
            if (i < idxes.size() && idxes.get(i) == secondIdx) {
                aircraft.add(humans.get(secondIdx));
                humans.set(secondIdx, null);
                i++;
            } else {
                if (secondIdx != firstIdx) {
                    humans.set(firstIdx, humans.get(secondIdx));
                }
                firstIdx++;
            }
        }
        humans = humans.subList(0, humans.size() - idxes.size());
        return aircraft;
    }

    List<Human> sendRandomPeople() {
        List<Integer> IDs = new ArrayList<>();
        for (int i = 0; i < humans.size(); i++) {
            if (randGenerator.nextDouble() < FLYING_RATIO) {
                IDs.add(i);
            }
        }
        return sendPeople(IDs);
    }

    void receivePeople(List<Human> people) {
        humans.addAll(people);
    }

    int size() {
        return humans.size();
    }
}
