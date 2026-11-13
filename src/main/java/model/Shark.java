package model;

import java.util.List;

public final class Shark extends Animal {
    public static final int BREEDING_TURNS = 10;
    public static final int STARVATION_TURNS = 3;
    private int hunger;

    Shark(Position position) { super(position); }

    @Override void act(World world) {
        ageForBreeding();
        List<Position> prey = world.preyNeighbors(position(), this);
        Position destination;
        if (!prey.isEmpty()) {
            destination = choose(prey);
            world.removeAt(destination);
            hunger = 0;
        } else {
            destination = choose(world.emptyNeighbors(position()));
            hunger++;
        }
        if (destination != null) {
            Position old = position();
            world.move(this, destination);
            if (ageSinceBreeding() >= BREEDING_TURNS) {
                world.add(new Shark(old));
                resetBreedingAge();
            }
        }
        if (hunger >= STARVATION_TURNS) world.remove(this);
    }

    @Override public String species() { return "shark"; }
    @Override boolean edibleBy(Shark shark) { return false; }
    @Override int tunaPopulationContribution() { return 0; }
    @Override int sharkPopulationContribution() { return 1; }
    @Override Animal offspring(Position position) { return new Shark(position); }
}
