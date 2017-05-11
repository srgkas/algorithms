import java.awt.*;
import java.util.*;
import javax.swing.*;

public class LagrangePolynomial {
    public static void main(String[] a) {
        MyJFrame f = new MyJFrame();
        f.setTitle("Drawing");
        f.setBounds(100,50,1000,800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    static class Point {
        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    static class MyJFrame extends JFrame {
        int radius = 4;
        int scale = 15;

        public void paint(Graphics g) {
            LinkedHashSet<Point> points = new LinkedHashSet<>();
            points.add(new Point(1,1));
            points.add(new Point(2,8));
            points.add(new Point(3,10));
            points.add(new Point(4,4));
//            points.add(new Point(4,3));
//            points.add(new Point(6,18));
//            points.add(new Point(10,2));
//            points.add(new Point(12,12));
            int height = 580;
            int width = 480;
            int xStart = 30;
            int yStart = 30;
            g.drawString("Y", xStart, yStart);
            g.drawLine(xStart, yStart,xStart, height);

            g.drawString("X", width, height);
            g.drawLine(xStart, height, width, height);

            ArrayList<Point> curve = getCurve(points, 1, 4, 1);
            System.out.println(curve);
            Point p = curve.get(0);
            g.fillRoundRect(xStart + (int)p.x * scale, height - (int)p.y*scale, radius, radius, radius, radius/2);
            for(int i = 1; i < curve.size(); i++) {
                p = curve.get(i);
                g.fillRoundRect(xStart + (int)p.x * scale, height - (int)p.y*scale, radius, radius, radius, radius/2);
                Point prev = curve.get(i-1);
//                System.out.println(((int)prev.x + xStart) + "->" + ((int)p.x + xStart) + "; " +(height - (int)prev.y ) + "->" + (height - (int)p.y));
                g.drawLine(xStart + (int)prev.x * scale, height - (int)prev.y*scale, xStart + (int)p.x*scale, height - (int)p.y*scale);
            }
        }

        public float getY(float x, float[] formula) {
            float y = 0;
            for (int i = 0; i < formula.length; i++) {
                y += Math.pow(x, i) * formula[i];
            }

            return y;
        }

        public ArrayList<Point> getCurve(LinkedHashSet<Point> src, int start, int end, float diff) {
            ArrayList<Point> result = new ArrayList<>();
            float[] formula = getFormula(src);

            printFormula(formula);
            for (float x = start; x <= end; x+=diff) {
                result.add(new Point(x, getY(x, formula)));
            }
            return result;
        }

        public float[] getFormula(LinkedHashSet<Point> points) {
            int n = points.size();
            Object[] src = points.toArray();
            float[] result = new float[n];
            for(int i=0; i < n; i++) {
                float[] formula = new float[n];
                float baseX = ((Point)src[i]).x;
                float baseY = ((Point)src[i]).y;
                float coeff = baseY;
                for (int j=0, formulaIndex = 0; j < src.length; j++) {
                    Point p = ((Point)src[j]);
                    if (i != j) {
                        coeff = coeff / (baseX - p.x);
                        if (formulaIndex != 0){
                            float[] temp = new float[formulaIndex + 2];
                            System.arraycopy(formula, 0, temp, 1, temp.length - 1);
                            for (int k = 0; k < temp.length; k ++) {
                                temp[k] += formula[k] * (-p.x);
                            }
                            System.arraycopy(temp, 0, formula, 0, temp.length);
                        } else {
                            formula[formulaIndex] = (-p.x);
                            formula[formulaIndex +1] = 1;
                        }
                        formulaIndex++;
                    }
                }
                for(int k = 0; k < formula.length; k++) {
                    formula[k] *= coeff;
                    result[k] += formula[k];
                }
            }
            return result;
        }

        public void printFormula(float[] formula) {
            for(int i=formula.length - 1; i >=0; i--) {
                if (i < formula.length -1) {
                    System.out.print(formula[i] > 0.0f ? "+" : "");
                }

                if (formula[i] != 0.0f) {
                    System.out.print(formula[i] != 1 ? formula[i] : "");
                    System.out.print((i > 0 ? "x^" + i : "") );
                }
            }
            System.out.println();
        }
    }
}
