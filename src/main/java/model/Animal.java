package model;

import java.util.List;

public abstract class Animal {
    private Position position;
    private int breedingAge;

    protected Animal(Position position) { this.position = position; }

    public final Position position() { return position; }
    protected final int ageSinceBreeding() { return breedingAge; }
    protected final void ageForBreeding() { breedingAge++; }
    protected final void resetBreedingAge() { breedingAge = 0; }
    final void moveTo(Position destination) { position = destination; }
    abstract void act(World world);
    public abstract String species();
    abstract boolean edibleBy(Shark shark);
    abstract int tunaPopulationContribution();
    abstract int sharkPopulationContribution();
    abstract Animal offspring(Position position);

    protected final Position choose(List<Position> positions) {
        return positions.isEmpty() ? null : positions.get(World.randomIndex(positions.size()));
    }
}
