package fr.nicolasgdj.rosolver;

import fr.nicolasgdj.rosolver.arguments.ArgumentParser;
import fr.nicolasgdj.rosolver.arguments.EOption;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        try {
            new ArgumentParser(args).parse();
        }catch (IllegalArgumentException ex){
            System.err.println(ex.getMessage());
            EOption.HELP.set(true);
        }

        if(EOption.HELP.getAsBoolean()) {
            System.out.println("Usage: a.java [options] <PLFile>");
            System.out.println("Options disponibles: ");
            System.out.println(" -s --simplexe : Résoue avec la méthode du simplexe pour résoudre le PL (à spécifier en tableau)");
            System.out.println(" -p --pl : Résoue un pl en sa forme standard.");
            System.out.println(" -h --help : Affiche ce message d'aide.");
            System.out.println();
            return;
        }

        if(EOption.SOLVE_PL.getAsBoolean()) {
            Scanner scanner = new Scanner(new File(EOption.PL.getAsString()));
            SystemPL system = new SystemPL();
            List<PL> pls = new ArrayList<>();
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.isBlank())
                    break;
                pls.add(PL.parse(line));
            }
            if(pls.size() < 2) {
                System.out.println("Pas assez d'équation pour résoudre le problème...");
                return;
            }
            for(int i = 0; i < pls.size()-1; ++i) {
                system.add(pls.get(i));
            }
            system.setObj(pls.get(pls.size()-1));
            system.resolve();
            return;
        }else if(EOption.SIMPLEXE.getAsBoolean()) {
            Scanner scanner = new Scanner(new File(EOption.PL.getAsString()));
            List<String> lines = new ArrayList<String>();
            int require = -1;

            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.isBlank()) {
                    break;
                }
                if(require == -1) {
                    require = line.split(" ").length;
                } else if(line.split(" ").length != require) {
                    System.out.println("ERREUR dans la saisie de '"+line+"', nombre d'entrée requis : " + require + ".");
                    return;
                }
                lines.add(line);
            }
            scanner.close();

            Fraction[][] matrix = new Fraction[require] [lines.size()];
            int y = 0;
            for(String line : lines) {
                int x = 0;
                for(String s : line.split(" ")){
                    matrix[x][y] = new Fraction(Integer.parseInt(s));
                    ++x;
                }
                ++y;
            }
            SimplexeSolver.solve(matrix);
            return;
        }

        System.out.println("Usage incorrect. --help pour plus d'informations.");
    }

}
