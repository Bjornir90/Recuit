package main.java.com.scep;

import java.util.ArrayList;
import java.util.List;


// La librairie Cplex du coup c'est IloCplex, j'ai aps encore vrmt vu comment ça marchait
public class Cplex implements Solver {

    private List<Integer> C, V, W, K, P, X;
    private List<List<Integer>> Iminus, Iplus, Ominus;
    private List<List<List<Integer>>> Beta, Ksi;

    public Cplex(
            List<Integer> c,
            List<Integer> v,
            List<Integer> w,
            List<Integer> k,
            List<Integer> p,
            List<List<List<Integer>>> beta,
            List<List<List<Integer>>> ksi)
    {

        C = c;
        V = v;
        W = w;
        K = k;
        P = p;
        Beta = beta;
        Ksi = ksi;
        X = new ArrayList<>();
    }

    public List<Integer> compute() {
        return null;
    }
}
