import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

public class ChessBoard extends JFrame {

    private static int resolution = 5;
    private static int sectorSize = 50;
    static long count = 0;
    private Set<Point> points;

    public void paint(Graphics g) {
        int xStart = 50;
        int yStart = 50;
        g.setColor(new Color(96, 143, 255));
        g.drawRect(xStart, yStart, resolution*sectorSize, resolution*sectorSize);

        for (int i = 0; i < resolution; i++) {
            if (i > 0) {
                g.drawLine(xStart, yStart + i * sectorSize, xStart + sectorSize * resolution, yStart + i * sectorSize);
                g.drawLine(xStart + i * sectorSize, yStart + sectorSize * resolution, xStart + i * sectorSize, yStart);
            }

            int k = i % 2;
            int count = Math.round((float)resolution/2);

            if (k == 1) {
                count -= resolution % 2;
            }

            for (int j = 0; j < count; j ++ ) {
                int x = j * 2 + k;
                g.fillRect(xStart + sectorSize * x, yStart + i * sectorSize,
                    sectorSize, sectorSize);
            }
        }

        g.setColor(new Color(0,0,0));
        Iterator<Point> iterator = points.iterator();

        for (Point end = iterator.next(); iterator.hasNext();) {
            Point start = end;
            end = iterator.next();
            g.fillOval(xStart + start.x * sectorSize - 2 - sectorSize/2, yStart + start.y*sectorSize - 2- sectorSize/2, 4,4);
            g.drawLine(
                    xStart + start.x * sectorSize - sectorSize/2, yStart + start.y*sectorSize - sectorSize/2,
                    xStart + end.x * sectorSize - sectorSize/2, yStart + end.y*sectorSize - sectorSize/2
            );
        }
    }

    public static void main(String[] args) {
        Board board = new Board(resolution);
        Horse horse = new Horse(board, new Point(2,2));
        boolean success = horse.goThrough();

        if (success) {
            ChessBoard chessBoard = new ChessBoard();
            chessBoard.points = board.visited;
            chessBoard.setTitle("ChessBoard");
            chessBoard.setBounds(100,50,1000,800);
            chessBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            chessBoard.setVisible(true);
        }
    }

    static class Board {
        public int resolution;
        public List<Point> points;
        public Set<Point> visited = new LinkedHashSet<>();
        public int freePoints;

        public Board(int resolution) {
            this.resolution = resolution;
            points = new ArrayList<>(resolution);
            for (int i = 1; i<= resolution; i++) {
                for (int j = 1; j <= resolution; j++) {
                    points.add(new Point(i, j));
                }
            }
            freePoints = resolution * resolution;
        }

        public boolean hasFreePoints() {
            return freePoints > 0;
        }

        @Override
        public String toString() {
            return "Board{" +
                    "resolution=" + resolution +
                    '}';
        }

        public boolean isValid(Point point) {
            boolean result = point.x > 0 && point.y > 0 && point.x <= resolution && point.y <= resolution;

            return result && !visited.contains(point);
        }
    }

    static class Point {
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            return ((Point)obj).x == x && ((Point)obj).y == y;
        }

        @Override
        public int hashCode() {
            return x*101 + y + 5 * x * y;
        }

        @Override
        public String toString() {
            return "Point{" + "x=" + x +", y=" + y +"}";
        }
    }

    enum StepDirection{
        RIGHT, LEFT, DOWN, UP
    }

    static class Horse {
        public Board board;
        public Point position;
        private static final int BIG_STEP = 2;
        private static final int SMALL_STEP = 1;
        public Logger logger = Logger.getLogger(getClass().getName());

        public Horse(Board board) {
            this(board, new Point(1,1));
        }

        public Horse(Board board, Point position) {
            board.visited.add(position);
            board.freePoints--;
            this.board = board;
            this.position = position;
        }

        public Point step(StepDirection direction, boolean positive) {
            switch (direction) {
                case DOWN:
                    return new Point(position.x + (positive ? 1 : -1) * SMALL_STEP, position.y - BIG_STEP);
                case UP:
                    return new Point(position.x + (positive ? 1 : -1) * SMALL_STEP, position.y + BIG_STEP);
                case RIGHT:
                    return new Point(position.x + BIG_STEP, position.y + (positive ? 1 : -1) * SMALL_STEP);
                case LEFT:
                    return new Point(position.x - BIG_STEP, position.y + (positive ? 1 : -1) * SMALL_STEP);
                default:
                    return position;
            }
        }

        public boolean goThrough(long depth) {
//            if (count % 1000000 == 0) {
//                logger.info(String.valueOf(count));
//            }

            if (!board.hasFreePoints()) {
                logger.info(String.valueOf(depth));
                return true;
            }
            count++;
            Point oldPosition = position;
            for (StepDirection direction: StepDirection.values()) {
                boolean secondDirection = false;
                do {
                    secondDirection = !secondDirection;
                    Point nextPoint = step(direction, secondDirection);
                    if (board.isValid(nextPoint)) {
                        position = nextPoint;
                        board.freePoints--;
                        board.visited.add(position);
                        boolean success = goThrough(++depth);
                        if (!success) {
                            board.freePoints++;
                            board.visited.remove(position);
                            position = oldPosition;
                        } else {
                            return true;
                        }
                    }
                } while (secondDirection);
            }

            return false;
        }

        public boolean goThrough() {
            return goThrough(1L);
        }
    }
}
