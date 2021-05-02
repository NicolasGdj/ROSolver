package fr.nicolasgdj.rosolver;

import java.util.ArrayList;
import java.util.List;

public class SystemPL {

    List<PL> pls = new ArrayList<>();
    PL init_obj = null;
    PL obj = null;

    public SystemPL() {

    }

    public void add(PL pl) {
        pls.add(pl);
    }

    public int getMaxIndice() {
        int max = 0;
        for(PL pl : pls) {
            if(pl.getMaxIndice() > max)
                max = pl.getMaxIndice();
        }
        return max;
    }

    public void setObj(PL plObj) {
        this.obj = plObj;
    }

    public boolean isCanonique() {
        for(PL pl : pls) {
            if(!pl.isCanonique())
                return false;
        }
        return true;
    }

    public void toCanonique() {
        if(isCanonique())
            return;
        for(PL pl : pls) {
            if(!pl.isCanonique()) {
                pl.mult(-1);
            }
        }
    }

    public boolean isNormal() {
        for(PL pl : pls) {
            if(!pl.isNormal())
                return false;
        }
        return true;
    }


    public void removeY() {
        for(PL pl : pls)
            pl.removeY();
    }

    public void toNormal() {
        if(isNormal())
            return;
        if(!isCanonique())
            toCanonique();
        int indice = getMaxIndice()+1;
        List<PL> needY = new ArrayList<>();
        for(PL pl : pls) {
            if(!pl.isNormal()) {
                pl.setOperation(PLType.EQ);
                pl.set(indice++, new Fraction(1));
                if(pl.getResult().val() < 0) {
                    pl.mult(-1);
                    needY.add(pl);
                }
            }
        }

        if(!needY.isEmpty()) {
            init_obj = obj;
            obj = (PL) obj.clone();
            for(int i = 1; i <= obj.getMaxIndice(); ++i) {
                obj.set(i, new Fraction(0));
            }
            obj.setResult(new Fraction(0));
        }
        for(PL pl : needY) {
            obj.set(indice, new Fraction(-1));
            obj.setY(indice);
            pl.setY(indice);
            pl.set(indice++, new Fraction(1));
            obj.add(pl);
        }



    }

    public void resetObj() {
        if(init_obj != null) {
            obj = init_obj;
            init_obj = null;
        }
    }

    public void show(){
        for(PL pl : pls) {
            System.out.println(pl);
        }
        System.out.println(obj);
    }

    public Fraction[][] toArray() {
        Fraction[][] matrix = new Fraction[getMaxIndice()+1][pls.size()+(obj == null ? 0 : 1)];
        int y = 0;
        for(PL pl : pls) {
            int x = 0;
            for(Fraction value : pl.getValues()) {
                matrix[x][y] = value;
                ++x;
            }
            while(x < getMaxIndice()){
                matrix[x][y] = new Fraction(0);
                ++x;
            }
            matrix[x][y] = pl.getResult();
            ++y;
        }
        if(obj != null){
            int x = 0;
            for(Fraction value : obj.getValues()) {
                matrix[x][y] = value;
                ++x;
            }
            while(x < getMaxIndice()){
                matrix[x][y] = new Fraction(0);
                ++x;
            }
            matrix[x][y] = obj.getResult();
        }
        return matrix;
    }

    public void resolve() {
        System.out.println(" -=- System -=-");
        show();
        System.out.println();

        if(isCanonique()) {
            System.out.println(" => Le system est déjà canonique.\n");
        } else {
            toCanonique();
            System.out.println(" -=- Canonique -=-");
            show();
            System.out.println();
        }
        System.out.println();

        toNormal();
        boolean revise = false;
        if(init_obj != null) {
            revise = true;
            System.out.println(" => Le system nécessite l'ajout d'un ou plusieurs y\n");
        }
        System.out.println(" -=- Standard -=-");
        show();
        System.out.println();

        System.out.println("Résolution par la méthode du simplexe :");

        Fraction[][] matrix = toArray();
        SimplexeSolver.solve(matrix);

        if(!revise) {
            return;
        }
        removeY();
        resetObj();
        System.out.println();
        System.out.println(" => On supprime les Y");
        System.out.println();
        List<Integer> base = SimplexeSolver.getBase(matrix);

        Fraction[][] tmp = toArray();
        for(int y = 0; y < pls.size(); ++y) {
            for(int x = 0; x < tmp.length-1; ++x) {
                tmp[x][y] = matrix[x][y];
            }
            tmp[tmp.length-1][y] = matrix[matrix.length-1][y];
        }
        matrix = tmp;
        SimplexeSolver.show(matrix);
        boolean needAdjustment = false;
        for(int b : base) {
            if(matrix[b-1][pls.size()].getNum() != 0) {
                needAdjustment = true;
                Fraction k;
                int ligne = 0;
                for(int y = 0; y < pls.size(); ++y) {
                    if(matrix[b-1][y].val() == 1) {
                        ligne = y;
                        break;
                    }
                }
                k = matrix[b-1][pls.size()].divide(matrix[b-1][ligne]);
                System.out.println(" => c" + PL.getIndice(b)  + "!= 0 | On doit faire: objectif - "  + k + "*l" + PL.getIndice(ligne+1));

                for(int x = 0; x < matrix.length; ++x) {
                    matrix[x][pls.size()] = matrix[x][pls.size()].sub(k.mult(matrix[x][ligne]));
                }
            }
        }
        if(needAdjustment) {
            System.out.println();
            System.out.println("On doit résoudre le nouveau programme avec la méthode du simplexe !");
            SimplexeSolver.solve(matrix);
        }

    }


}
