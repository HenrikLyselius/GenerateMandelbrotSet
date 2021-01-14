package com.lyselius.mandelbrotserver.computation;

import com.lyselius.mandelbrotserver.MandelbrotResult;

import java.util.ArrayList;
import java.util.List;

public class MandelbrotCalc {



    public static MandelbrotResult getResults(double C_re_min, double C_re_max, double C_im_min,
                                              double C_im_max, int x_dim, int y_dim,
                                              int maxNumberOfIterations)
    {

        double C_re_step = (C_re_max - C_re_min) / (double) x_dim;
        double C_im_step = (C_im_max - C_im_min) / (double) y_dim;
        List<Integer> greyScaleValues = new ArrayList<>();

        // Compute correct greyscale value for each point.
        for(int x = 0; x < x_dim; x++)
        {
            for(int y = 0; y < y_dim; y++)
            {
                int iterations = getNumberOfIterations(C_re_min + (x * C_re_step), C_im_min + (y * C_im_step),
                                                        maxNumberOfIterations);
                int greyScale = (int) ((iterations / (double) maxNumberOfIterations) * 255);
                greyScaleValues.add(greyScale);
            }
        }

        return new MandelbrotResult(greyScaleValues);
    }



    private static int getNumberOfIterations(double C_re, double C_im, int maxNumberOfIterations)
    {
        double Z_re = C_re;
        double Z_im = C_im;

        for(int i = 0; i < maxNumberOfIterations; i++)
        {
            if(absoluteValueOver2(Z_re, Z_im))
            {
                return i;
            }

            // Compute Z values for next iteration.
            double Z_re_help = ( (Z_re * Z_re) - (Z_im * Z_im) ) + C_re;
            Z_im = 2 * (Z_re * Z_im)  + C_im;
            Z_re = Z_re_help;
        }

        return maxNumberOfIterations;
    }



    private static boolean absoluteValueOver2(double Z_re, double Z_im)
    {
        if(( (Z_re * Z_re) + (Z_im * Z_im) ) > 4)
        {
            return true;
        }
        return false;
    }
}
