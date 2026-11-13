package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class World {
    public static final double INITIAL_TUNA_FRACTION = 0.30;
    public static final double INITIAL_SHARK_FRACTION = 0.03;
    private static final Random RANDOM = new Random();
    private final int width;
    private final int height;
    private final Animal[][] cells;
    private int turn;

    public World(int width, int height) {
        if (width < 1 || height < 1) throw new IllegalArgumentException("Grid dimensions must be positive");
        this.width = width; this.height = height; cells = new Animal[height][width];
        populate();
    }

    private void populate() {
        List<Position> positions = new ArrayList<>();
        for (int y=0; y<height; y++) for (int x=0; x<width; x++) positions.add(new Position(x,y));
        Collections.shuffle(positions, RANDOM);
        int tunaCount = (int)(positions.size() * INITIAL_TUNA_FRACTION);
        int sharkCount = (int)(positions.size() * INITIAL_SHARK_FRACTION);
        for (int i=0; i<tunaCount; i++) add(new Tuna(positions.get(i)));
        for (int i=tunaCount; i<tunaCount+sharkCount; i++) add(new Shark(positions.get(i)));
    }

    public void advance() {
        List<Animal> actors = animals();
        Collections.shuffle(actors, RANDOM);
        for (Animal animal : actors) if (contains(animal)) animal.act(this);
        turn++;
    }

    public int turn() { return turn; }
    public int width() { return width; }
    public int height() { return height; }
    public List<Animal> animals() {
        List<Animal> result = new ArrayList<>();
        for (Animal[] row : cells) for (Animal animal : row) if (animal != null) result.add(animal);
        return result;
    }
    public int tunaCount() { return animals().stream().mapToInt(Animal::tunaPopulationContribution).sum(); }
    public int sharkCount() { return animals().stream().mapToInt(Animal::sharkPopulationContribution).sum(); }
    public int emptyCount() { return width * height - tunaCount() - sharkCount(); }
    public Animal animalAt(int x, int y) { return cells[y][x]; }

    List<Position> emptyNeighbors(Position p) { return neighbors(p).stream().filter(n -> at(n) == null).toList(); }
    List<Position> preyNeighbors(Position p, Shark hunter) { return neighbors(p).stream().filter(n -> at(n) != null && at(n).edibleBy(hunter)).toList(); }
    private List<Position> neighbors(Position p) {
        List<Position> result = new ArrayList<>(4);
        addUnique(result, new Position(p.x(), Math.floorMod(p.y()-1,height)));
        addUnique(result, new Position(p.x(), Math.floorMod(p.y()+1,height)));
        addUnique(result, new Position(Math.floorMod(p.x()-1,width),p.y()));
        addUnique(result, new Position(Math.floorMod(p.x()+1,width),p.y()));
        result.remove(p);
        return result;
    }
    private void addUnique(List<Position> positions, Position p) { if (!positions.contains(p)) positions.add(p); }
    private Animal at(Position p) { return cells[p.y()][p.x()]; }
    void move(Animal animal, Position destination) {
        Position origin = animal.position();
        cells[origin.y()][origin.x()] = null;
        animal.moveTo(destination);
        cells[destination.y()][destination.x()] = animal;
    }
    void add(Animal animal) { Position p=animal.position(); cells[p.y()][p.x()]=animal; }
    void remove(Animal animal) { Position p=animal.position(); if (cells[p.y()][p.x()] == animal) cells[p.y()][p.x()]=null; }
    void removeAt(Position p) { cells[p.y()][p.x()] = null; }
    private boolean contains(Animal animal) { Position p=animal.position(); return cells[p.y()][p.x()] == animal; }
    static int randomIndex(int bound) { return RANDOM.nextInt(bound); }
}
