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

    private ArrayBlockingQueue<String> latestFreeServer = new ArrayBlockingQueue<String>(20);



    public LoadBalancer()
    {
        servers.add("http://localhost:8090");
    }


    // Distributes the servers evenly to start with, and thereafter gives every thread the server
    // with the latest free spot.
    public String getServer()
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
        try { return latestFreeServer.take(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return getRandomServer();
    }

    public void setLatestFreeServer(String server) {
        latestFreeServer.add(server);
    }

    public String getRandomServer()
    {
        Random random = new Random();
        return servers.get(random.nextInt(servers.size()));
    }
}
