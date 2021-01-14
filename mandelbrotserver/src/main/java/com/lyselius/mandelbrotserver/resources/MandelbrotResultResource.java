package com.lyselius.mandelbrotserver.resources;


import com.lyselius.mandelbrotserver.MandelbrotResult;
import com.lyselius.mandelbrotserver.computation.MandelbrotCalc;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MandelbrotResultResource {

    private int counter = 0;


    @RequestMapping(method= RequestMethod.GET, value ="/mandelbrot/{C_re_min}/{C_re_max}/{C_im_min}/{C_im_max}/{x_dim}/{y_dim}/{maxNumberOfIterations}")
    public ResponseEntity<MandelbrotResult> calculateMandelbrot(@PathVariable double C_re_min,
                                                               @PathVariable double C_re_max,
                                                               @PathVariable double C_im_min,
                                                               @PathVariable double C_im_max,
                                                               @PathVariable int x_dim,
                                                               @PathVariable int y_dim,
                                                               @PathVariable int maxNumberOfIterations)
    {
        MandelbrotResult mandelbrotResult = MandelbrotCalc.getResults(C_re_min, C_re_max, C_im_min, C_im_max,
                x_dim, y_dim, maxNumberOfIterations);

        System.out.println("Counter is: " + counter);
        counter++;

        // To test handling of timeouts and server errors.
       /* if(counter == 200)
        {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/

        return ResponseEntity.ok(mandelbrotResult);
    }
}
