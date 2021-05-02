package fr.nicolasgdj.rosolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SimplexeSolver {

    public static List<Integer> getBase(Fraction[][] matrix) {
        if(matrix.length == 0)
            return Arrays.asList();
        ArrayList<Integer> list = new ArrayList<Integer>();
        x_for: for(int x = 0; x < matrix.length; ++x) {
            int numberOfOne = 0;
            for(int y = 0; y < matrix[x].length; ++y) {
                if(matrix[x][y].val() != 1.0 && matrix[x][y].val() != 0.0)
                    continue x_for;
                if (matrix[x][y].val() == 1.0) {
                    ++numberOfOne;
                }
            }
            if(numberOfOne == 1){
                list.add(x+1);
            }
        }
        return list;
    }

    public static List<Fraction> getResult(Fraction[][] matrix) {
        List<Integer> base = getBase(matrix);
        List<Fraction> result = new ArrayList<>();
        for(int x = 0; x < matrix.length-1; ++x) {
            Fraction fraction = new Fraction(0);
            if(base.contains(x+1)){
                for(int y = 0; y < matrix[x].length; ++y) {
                    if(matrix[x][y].val() == 1) {
                        fraction = matrix[matrix.length-1][y];
                        break;
                    }
                }
            }
            result.add(fraction);
        }
        return result;
    }

    public static void show(Fraction[][] matrix) {
        if(matrix.length == 0){
            System.out.println();
            return;
        }

        int[] spaces = new int[matrix.length];

        for(int x = 0; x < matrix.length; ++x) {
            int biggest = 0;
            for(int y = 1; y < matrix[0].length; ++y) {
                if(matrix[x][y].toString().length() > matrix[x][biggest].toString().length()) {
                    biggest = y;
                }
            }
            spaces[x] = matrix[x][biggest].toString().length();
        }

        String basicLine = "";
        for (int y = 0; y < matrix[0].length; ++y) {

            if(y == matrix[0].length-1) {
                System.out.println("-".repeat(basicLine.length()));
            }
            basicLine = "";
            for(int x = 0; x < matrix.length; ++x) {
                int requireSize = spaces[x];
                String value = matrix[x][y].toString();
                basicLine += (matrix.length -1 == x ? "| " : "") +   " ".repeat(requireSize - value.length()) + value + " ";
            }
            System.out.println(basicLine);
        }
        System.out.println();

    }

    public static int getNewPivotColumn(Fraction[][] matrix){
        if(matrix.length == 0)
            return -1;
        int best = -1;
        int y = matrix[0].length-1;
        for(int x = 0; x < matrix.length-1; ++x) {
            double val = matrix[x][y].val();
            if(val > 0 && (best == -1 || matrix[best][y].val() <= val)){
                best = x;
            }
        }
        return best;
    }

    public static int getNewPivotLine(Fraction[][] matrix, int column){
        if(matrix.length == 0)
            return -1;

        int min = -1;
        double minValue = Double.MAX_VALUE;
        for(int y = 0; y < matrix[column].length-1; ++y) {
            double val = matrix[matrix.length-1][y].val()/matrix[column][y].val();
            if(val >= 0 && minValue >= val){
                min = y;
                minValue = val;
            }
        }
        return min;
    }

    public static void solve(Fraction[][] matrix) {
        System.out.println("0# INITIALISATION : ");
        show(matrix);

        int i = 1;
        int x_pivot, y_pivot;
        while((x_pivot = getNewPivotColumn(matrix)) != -1) {
            y_pivot = getNewPivotLine(matrix, x_pivot);
            Fraction pivot = matrix[x_pivot][y_pivot];
            System.out.println(" => Pivot: " + pivot + " (" + (x_pivot+1) + ";" + (y_pivot+1) + ")");
            System.out.println("    Base: " + getBase(matrix));
            System.out.println();

            Fraction[][] old = new Fraction[matrix.length] [matrix[0].length];
            for(int x = 0; x < matrix.length; ++x) {
                for (int y = 0; y < matrix[x].length; ++y) {
                    old[x][y] = matrix[x][y];
                }
            }
            for(int x = 0; x < matrix.length; ++x) {
                for(int y = 0; y < matrix[x].length; ++y) {
                    if(y == y_pivot) {
                        matrix[x][y] = old[x][y].divide(pivot);
                    } else {
                        matrix[x][y] = old[x][y].mult(pivot).sub(old[x_pivot][y].mult(old[x][y_pivot])).divide(pivot);
                    }
                }
            }

            System.out.println(i + "# Etape "+i+" : ");
            show(matrix);
            ++i;
        }

        System.out.println(i +"# Fin:");
        System.out.println("  Base: " + getBase(matrix));
        System.out.println("  Solution dans la base: " + getResult(matrix));
        System.out.println("  Solution associée: " + matrix[matrix.length-1][matrix[0].length-1].abs());


    }


}
