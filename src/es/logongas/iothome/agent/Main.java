/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.logongas.iothome.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.logongas.iothome.agent.http.Http;
import es.logongas.iothome.modelo.Measure;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author logongas
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException, FileNotFoundException, IOException {

        while (1 == 1) {

            Http http = new Http();
            URL url = new URL("http://localhost:8084/iothome/api/Measure");

            Random random = new Random();
            double d1 = 1000 + (random.nextGaussian() * 200);
            double d2 = 1000 + (random.nextGaussian() * 200);
            double d3 = 1000 + (random.nextGaussian() * 200);

            Measure measure = new Measure();
            measure.getDevice().setIdDevice(1);
            measure.setTime(new Date());
            measure.setStream0(d1);
            measure.setStream1(d2);
            measure.setStream2(d3);

            
            
            //Guardarlo a fichero
                        ObjectMapper objectMapper=new ObjectMapper();
            
            
            

            OutputStream outputStream=new BufferedOutputStream(new FileOutputStream("measures.json"));
            objectMapper.writeValue(outputStream,measure);
            
            http.post(url, measure);

            try {
                Thread.sleep(5000);                
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
