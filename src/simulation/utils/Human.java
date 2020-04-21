package simulation.utils;

import java.util.Random;

class Human {
    private int antigenIterations = 0;
    private boolean susceptible = true;
    private boolean vaccinated = false;
    private Virus infection;

    boolean isInfected() {
        return infection != null;
    }

    boolean isSusceptible() {
        return susceptible;
    }

    boolean isVaccinated() {
        return vaccinated;
    }

    boolean isHealthy() {
        return susceptible && !isInfected();
    }

    int getAntigenIterations() {
        return antigenIterations;
    }

    Virus getInfection() {
        return infection;
    }

    boolean tryToImmunize(Random randomGenerator) {
        if (randomGenerator.nextDouble() * antigenIterations > 0.9) {
            infection = null;
            susceptible = false;
            return true;
        }
        antigenIterations++;
        return false;
    }

    boolean infect(Virus virus) {
        if (isHealthy()) {
            infection = virus;
            return true;
        }
        return false;
    }

    void vaccinate() {
        vaccinated = true;
    }

}
