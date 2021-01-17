package com.lyselius.mandelbrotclient;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeoutException;


public class MandelbrotCalc implements Runnable {

    private double C_re_min;
    private double C_re_max;
    private double C_im_min;
    private double C_im_max;
    private int x_dim;
    private int y_dim;
    private int x_start;
    private int y_start;
    private int maxNumberOfIterations;
    private ArrayBlockingQueue results;
    private LoadBalancer loadBalancer;


    public MandelbrotCalc(double C_re_min, double C_re_max, double C_im_min, double C_im_max,
                          int x_dim, int y_dim, int maxNumberOfIterations, int x_start, int y_start,
                          ArrayBlockingQueue results, LoadBalancer loadBalancer) {
        this.C_re_min = C_re_min;
        this.C_re_max = C_re_max;
        this.C_im_min = C_im_min;
        this.C_im_max = C_im_max;
        this.x_dim = x_dim;
        this.y_dim = y_dim;
        this.x_start = x_start;
        this.y_start = y_start;
        this.maxNumberOfIterations = maxNumberOfIterations;
        this.results = results;
        this.loadBalancer = loadBalancer;
    }


    @Override
    public void run()
    {
        String serverURI = loadBalancer.getServer();
        MandelbrotResult mandelbrotResult = null;

        while(true)
        {
            mandelbrotResult = callServer(serverURI);
            if(mandelbrotResult != null) { break; }
            // If something went wrong, assign a new server and try again.
            serverURI = loadBalancer.getRandomServer();
        }


        // Tell the load balancer that this server now has an open spot.
        loadBalancer.setLatestFreeServer(serverURI);

        // Update the result object with info about where in the final picture, this part should be placed.
        mandelbrotResult.setX_start(x_start);
        mandelbrotResult.setY_start(y_start);

        try { results.put(mandelbrotResult); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }






    private MandelbrotResult callServer(String serverURI){

        try {
            MandelbrotResult mandelbrotResult = WebClient.builder().build()
                    .get()
                    .uri(serverURI + "/mandelbrot/" + C_re_min + "/" + C_re_max + "/"
                            + C_im_min + "/" + C_im_max + "/" + x_dim + "/" + y_dim +
                            "/" + maxNumberOfIterations)
                    .retrieve()
                    .bodyToMono(MandelbrotResult.class)
                    .timeout(Duration.ofMillis(4000))
                    .block();

            return mandelbrotResult;
        }

         /* For some reason the compiler will not accept a Java.util.concurrent.exception
         in the catch clause, even though that is what is eventually caught in case of a timeout
         from the Mono. Look in to this later. */
        catch (WebClientResponseException e)
        {
            System.out.println(e.toString());
            return null;
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
            return null;
        }
    }

}