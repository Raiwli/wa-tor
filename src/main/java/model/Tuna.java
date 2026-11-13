package model;

import java.util.List;

public final class Tuna extends Animal {
    public static final int BREEDING_TURNS = 3;

    Tuna(Position position) { super(position); }

    @Override void act(World world) {
        ageForBreeding();
        List<Position> empty = world.emptyNeighbors(position());
        Position destination = choose(empty);
        if (destination == null) return;
        Position old = position();
        world.move(this, destination);
        if (ageSinceBreeding() >= BREEDING_TURNS) {
            world.add(new Tuna(old));
            resetBreedingAge();
        }
    }

    @Override public String species() { return "tuna"; }
    @Override boolean edibleBy(Shark shark) { return true; }
    @Override int tunaPopulationContribution() { return 1; }
    @Override int sharkPopulationContribution() { return 0; }
    @Override Animal offspring(Position position) { return new Tuna(position); }
}
