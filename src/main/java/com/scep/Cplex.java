package com.scep;

import java.util.List;


// La librairie Cplex du coup c'est IloCplex, j'ai aps encore vrmt vu comment ça marchait
public class Cplex implements Solver {

    private List<Integer> C, V, W, K, P, X;
    private List<List<Integer>> Iminus, Iplus, Ominus;
    private List<List<List<Integer>>> Beta, Ksi;

    public List<Integer> compute() {
        return null;
    }
}
