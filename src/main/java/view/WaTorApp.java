package view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Separator;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Animal;
import model.World;

import java.util.ArrayDeque;
import java.util.Deque;

public final class WaTorApp extends Application {
    private static final int CELL_SIZE = 10;
    private static final int PANEL_WIDTH = 280;
    private static final int HISTORY_LENGTH = 300;
    private static final Color BACKGROUND = Color.web("#f1f3ed");
    private static final Color EMPTY = Color.web("#8064a8");
    private static final Color TUNA = Color.web("#ffd43b");
    private static final Color SHARK = Color.web("#05070a");
    private final Deque<Integer> tunaHistory = new ArrayDeque<>();
    private final Deque<Integer> sharkHistory = new ArrayDeque<>();
    private World world;
    private Canvas sea;
    private Canvas graph;
    private javafx.scene.control.Label turnLabel, tunaLabel, sharkLabel, emptyLabel;
    private Timeline timeline;
    private boolean paused;

    @Override public void start(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        int columns = Math.max(1, (int)(bounds.getWidth() - PANEL_WIDTH) / CELL_SIZE);
        int rows = Math.max(1, (int)bounds.getHeight() / CELL_SIZE);
        world = new World(columns, rows);
        sea = new Canvas(columns * CELL_SIZE, rows * CELL_SIZE);
        graph = new Canvas(PANEL_WIDTH - 30, 260);
        turnLabel = label("Tour : 0", "#45586d", 12);
        tunaLabel = label("■ Thons : 0", "#d6a900", 12);
        sharkLabel = label("■ Requins : 0", "#05070a", 12);
        emptyLabel = label("Cases vides : 0", "#45586d", 12);
        javafx.scene.control.Label title = label("Wa-Tor", "#263b55", 20);
        javafx.scene.control.Label controlsTitle = label("COMMANDES", "#263b55", 12);
        javafx.scene.control.Label pauseControl = label("Espace  Pause / reprendre", "#45586d", 11);
        javafx.scene.control.Label quitControl = label("Échap    Quitter", "#45586d", 11);
        VBox panel = new VBox(7, title, turnLabel, tunaLabel, sharkLabel, emptyLabel,
                new Separator(), controlsTitle, pauseControl, quitControl, new Separator(), graph);
        panel.setStyle("-fx-background-color: #f1f3ed; -fx-padding: 18px 15px;");
        panel.setPrefWidth(PANEL_WIDTH); panel.setMinWidth(PANEL_WIDTH); panel.setMaxWidth(PANEL_WIDTH);
        HBox root = new HBox(sea, panel); root.setStyle("-fx-background-color: #f1f3ed;");
        Scene scene = new Scene(root, sea.getWidth() + PANEL_WIDTH, sea.getHeight(), BACKGROUND);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                paused = !paused;
                if (paused) timeline.pause(); else timeline.play();
            } else if (event.getCode() == KeyCode.ESCAPE) stage.close();
        });
        stage.setTitle("Wa-Tor"); stage.setScene(scene); stage.setFullScreen(true);
        draw(); updateStats(); stage.show();
        timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            world.advance(); remember(); draw(); updateStats();
        }));
        timeline.setCycleCount(Animation.INDEFINITE); timeline.play();
    }

    private javafx.scene.control.Label label(String text, String color, int size) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setTextFill(Color.web(color)); label.setFont(Font.font("Monospaced", size)); return label;
    }
    private void remember() {
        tunaHistory.addLast(world.tunaCount()); sharkHistory.addLast(world.sharkCount());
        if (tunaHistory.size() > HISTORY_LENGTH) { tunaHistory.removeFirst(); sharkHistory.removeFirst(); }
    }
    private void updateStats() {
        turnLabel.setText("Tour : " + world.turn()); tunaLabel.setText("■ Thons : " + world.tunaCount());
        sharkLabel.setText("■ Requins : " + world.sharkCount()); emptyLabel.setText("Cases vides : " + world.emptyCount());
        drawGraph();
    }
    private void draw() {
        GraphicsContext gc = sea.getGraphicsContext2D(); gc.setFill(EMPTY); gc.fillRect(0, 0, sea.getWidth(), sea.getHeight());
        for (Animal animal : world.animals()) {
            gc.setFill(animal.species().equals("tuna") ? TUNA : SHARK);
            gc.fillRect(animal.position().x() * CELL_SIZE + 1, animal.position().y() * CELL_SIZE + 1, CELL_SIZE - 1, CELL_SIZE - 1);
        }
    }
    private void drawGraph() {
        GraphicsContext gc = graph.getGraphicsContext2D(); double w=graph.getWidth(), h=graph.getHeight();
        gc.setFill(BACKGROUND); gc.fillRect(0,0,w,h);
        int max = Math.max(1, world.width() * world.height());
        double left=46, right=w-5, top=30, bottom=h-28;
        gc.setFont(Font.font("Monospaced", 9)); gc.setStroke(Color.web("#aab6c2")); gc.setFill(Color.web("#45586d"));
        gc.fillText("Population", left, 14);
        for (int i=0; i<=5; i++) {
            double y=bottom-(bottom-top)*i/5.0; gc.setLineDashes(2,3); gc.strokeLine(left,y,right,y); gc.setLineDashes();
            gc.fillText(Integer.toString(max*i/5), 0, y+3);
        }
        gc.strokeLine(left,top,left,bottom); gc.strokeLine(left,bottom,right,bottom);
        gc.fillText("Tour", (left+right)/2, h-5);
        drawSeries(gc, tunaHistory, TUNA, max, left, right, top, bottom);
        drawSeries(gc, sharkHistory, SHARK, max, left, right, top, bottom);
    }
    private void drawSeries(GraphicsContext gc, Deque<Integer> values, Color color, int max,
                            double left, double right, double top, double bottom) {
        if (values.size() < 2) return;
        gc.setStroke(color); gc.setLineWidth(1.3); int n=values.size(), i=0, previous=0;
        for (int value : values) {
            double x=left+(right-left)*i/(HISTORY_LENGTH-1.0), y=bottom-(bottom-top)*value/max;
            if (i>0) {
                double px=left+(right-left)*(i-1)/(HISTORY_LENGTH-1.0);
                double py=bottom-(bottom-top)*previous/max; gc.strokeLine(px,py,x,y);
            }
            previous=value; i++;
        }
    }
    @Override public void stop() { if (timeline != null) timeline.stop(); }
    public static void main(String[] args) { launch(args); }
}
