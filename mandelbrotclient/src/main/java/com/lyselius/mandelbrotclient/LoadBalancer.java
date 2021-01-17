package com.lyselius.mandelbrotclient;

import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

@Service
public class LoadBalancer {


    private int initialDistribution = 0;
    private List<String> servers = new ArrayList<>();
    private ArrayBlockingQueue<String> latestFreeServers = new ArrayBlockingQueue<String>(20);




    public LoadBalancer()
    {

    }


    // Distribute the servers evenly to start with, and then give every returning thread
    // the server with the latest free spot.
    public synchronized String getServer()
    {
        if(initialDistribution < 6 * servers.size())
        {
            String server = servers.get(initialDistribution % servers.size());
            initialDistribution++;
            return server;
        }
        return getLatestFreeServer();
    }

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }


    public String getLatestFreeServer() {
        try { return latestFreeServers.take(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return getRandomServer();
    }

    public void setLatestFreeServer(String server) {
        latestFreeServers.add(server);
    }

    public String getRandomServer()
    {
        Random random = new Random();
        return servers.get(random.nextInt(servers.size()));
    }

    public int getInitialDistribution() {
        return initialDistribution;
    }

    public void setInitialDistribution(int initialDistribution) {
        this.initialDistribution = initialDistribution;
    }


    public ArrayBlockingQueue<String> getLatestFreeServers() {
        return latestFreeServers;
    }

    public void setLatestFreeServers(ArrayBlockingQueue<String> latestFreeServers) {
        this.latestFreeServers = latestFreeServers;
    }
}
