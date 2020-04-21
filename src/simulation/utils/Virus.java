package simulation.utils;

public class Virus {
    private String name;
    private final double r0;
    private final double lethality;

    public Virus(String name, double r0, double lethality) {
        this.name = name;
        this.r0 = r0;
        this.lethality = lethality;
    }

    public String getName() {
        return name;
    }

    double getLethality() {
        return lethality;
    }

    double getR0() {
        return r0;
    }
}
