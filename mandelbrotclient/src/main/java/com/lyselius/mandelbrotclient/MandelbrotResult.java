package com.lyselius.mandelbrotclient;

import java.util.List;

public class MandelbrotResult {

    List<Integer> results;
    int x_start;
    int y_start;


    public MandelbrotResult(List<Integer> results, int x_start, int y_start)
    {
        this.results = results;
        this.x_start = x_start;
        this.y_start = y_start;
    }


    public MandelbrotResult()
    {

    }


    public List<Integer> getResults() {
        return results;
    }

    public void setResults(List<Integer> results) {
        this.results = results;
    }

    public int getX_start() {
        return x_start;
    }

    public void setX_start(int x_start) {
        this.x_start = x_start;
    }

    public int getY_start() {
        return y_start;
    }

    public void setY_start(int y_start) {
        this.y_start = y_start;
    }





}


