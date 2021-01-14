package com.lyselius.mandelbrotclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.*;



@Component
public class Start implements CommandLineRunner {


    @Autowired
    private LoadBalancer loadBalancer;

    private double C_re_min;
    private double C_re_max;
    private double C_im_min;
    private double C_im_max;
    private int x_dim;
    private int y_dim;
    private int numberOfIterations;
    private int divisions;
    private ArrayBlockingQueue<MandelbrotResult> results = new ArrayBlockingQueue<MandelbrotResult>(1000000);
    private BufferedImage image;
    private File file = new File("C:\\BilderJava\\pixelbildParallelliserad2.png");
    private Scanner scanner = new Scanner(System.in);


    public Start()
    {
        setDefaults();
        image = new BufferedImage(x_dim, y_dim, BufferedImage.TYPE_BYTE_GRAY);
    }


    private void setDefaults()
    {
        C_re_min = -2.25;
        C_re_max = 0.75;
        C_im_min = -1.5;
        C_im_max = 1.5;
        x_dim = 1000;
        y_dim = 1000;
        numberOfIterations = 20;
        divisions = 10;
        /*C_re_step = (C_re_max - C_re_min) / (double) x_dim;
        C_im_step = (C_im_max - C_im_min) / (double) y_dim;*/
    }




    public void calculateMandelbrot()
    {
        int numberOfThreads = 6 * loadBalancer.getServers().size();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfThreads);
        double C_re_step = (C_re_max - C_re_min) / (double) x_dim;
        double C_im_step = (C_im_max - C_im_min) / (double) y_dim;


        // Split the job in tasks to be calculated separately.
        for (int x = 0; x < x_dim / divisions; x++) {
            for (int y = 0; y < y_dim / divisions; y++) {
                MandelbrotCalc mc = new MandelbrotCalc(C_re_min + x * divisions * C_re_step,
                        C_re_min + (x + 1) * divisions * C_re_step,
                        C_im_min + y * divisions * C_im_step,
                        C_im_min + (y + 1) * divisions * C_im_step,
                        divisions, divisions, numberOfIterations,
                        x * divisions, y * divisions,
                        results, loadBalancer);

                executor.submit(mc);
            }
        }

        drawPicture();
    }




    private void drawPicture()
    {
        // This method takes the calculated image parts from the results queue, and coalesce them
        // to the final image.

        int parts = (x_dim / divisions) * (x_dim / divisions);

        for(int i = 0; i < parts; i++)
        {
            try {
                MandelbrotResult mandelbrotResult = results.take();

                int x_start = mandelbrotResult.getX_start();
                int y_start = mandelbrotResult.getY_start();

                for(int x = 0; x < divisions; x++)
                {
                    for(int y = 0; y < divisions; y++)
                    {
                        int greyScale = mandelbrotResult.getResults().get(x * divisions + y);
                        int RGB = greyScale << 16 | greyScale << 8 | greyScale;
                        image.setRGB(x_start + x, y_start + y, RGB);
                    }
                }
            }
            catch (InterruptedException e) { e.printStackTrace(); }

            //System.out.println("Parts fixed at this moment: " + (i + 1));
        }

        try
        {
            ImageIO.write(image, "png", file);
            System.out.println("The image is ready. \n");
        }
        catch(IOException e) {
            System.out.println("Something went wrong when writing the image to file.");
        }

        restartApplication();
    }



    private void restartApplication()
    {
        loadBalancer.getServers().clear();
        loadBalancer.getServers().add("http://localhost:8090");
        setDefaults();
        showMenu();
    }





    @Override
    public void run(String... args) throws Exception {
        showMenu();
    }



    private void showMenu()
    {
        System.out.println("Menu: \n" +
                "1. Run with default settings. \n" +
                "2. Manually enter values");
        checkMenuChoice();
    }



    private void getInputFromUser()
    {
        System.out.println("This is the default values. Copy and paste them, and make changes as you like. \n" +
                "Note that you can add additional servers, just leave a space between them. \n \n" +
                "C_re_min  C_re_max  C_im_min  C_im_max  x_pixels  y_pixels  n_max  divisions  list_of_servers \n" +
                "-2.25     0.75       -1.5      1.5       1000      1000      20     10        http://localhost:8090");

        readInputFromUser();
    }




    private void checkMenuChoice()
    {
        String string = scanner.nextLine();
        if(isInteger(string))
        {
            int i = Integer.parseInt(string);

            switch(i){
                case 1:
                    System.out.println("The calculation has started...\n");
                    calculateMandelbrot();
                    break;

                case 2:
                    getInputFromUser();
                    break;

                default:
                    System.out.println("You can only chose from the menu values. \n");
                    showMenu();
                    break;
            }
        }
        else
        {
            System.out.println("You can only chose from the menu values. \n");
            showMenu();
        }
    }


    private void readInputFromUser()
    {
        String string = scanner.nextLine();
        String[] input = string.split("\\s+");
        if(inputIsValid(input))
        {
            System.out.println("The calculation has started...\n");
            updateValues(input);
            calculateMandelbrot();
        }
        else
        {
            System.out.println("You entered invalid data.");
            showMenu();
        }
    }

    private void updateValues(String[] input)
    {
        C_re_min = Double.parseDouble(input[0]);
        C_re_max = Double.parseDouble(input[1]);
        C_im_min = Double.parseDouble(input[2]);
        C_re_max = Double.parseDouble(input[3]);
        x_dim = Integer.parseInt(input[4]);
        y_dim = Integer.parseInt(input[5]);
        numberOfIterations = Integer.parseInt(input[6]);
        divisions = Integer.parseInt(input[7]);

        loadBalancer.getServers().clear();

        for(int i = 8; i < input.length; i++)
        {
            loadBalancer.getServers().add(input[i]);
        }
    }


    private boolean inputIsValid(String[] input)
    {
        if(input.length < 9)
        {
            return false;
        }

        for(int i = 0; i < 4; i++)
        {
            if(!isDouble(input[i])) { return false; }
        }

        for(int i = 4; i < 8; i++)
        {
            if(!isInteger(input[i])) { return false; }
        }

        for(int i = 8; i < input.length; i++)
        {
            if(!input[i].substring(0,4).equals("http")) { return false; }
        }

        return true;
    }


    private boolean isInteger(String string) {
        int i;
        try { i = Integer.parseInt(string); }
        catch (NumberFormatException e) { return false; }
        return true;
    }

    private boolean isDouble(String string) {
        double d;
        try { d = Double.parseDouble(string); }
        catch (NumberFormatException e) { return false; }
        return true;
    }
}
