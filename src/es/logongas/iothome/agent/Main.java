/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.logongas.iothome.agent;

import es.logongas.iothome.agent.http.Http;
import es.logongas.iothome.agent.storage.Storage;
import es.logongas.iothome.modelo.Measure;
import es.logongas.iothome.agent.arduino.Microcontroller;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author logongas
 */
public class Main {

    static Microcontroller microcontroller = null;
    static Config config;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            inicialize(args);

            while (true) {

                try {

                    Measure measure = getMeasure();

                    saveMeasure(measure);

                    sendMeasures();

                    try {
                        Thread.sleep(config.getSleep());
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }



                } catch (Exception ex) {
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
                    System.out.println(sdf.format(new Date()));
                    ex.printStackTrace();
                }

            }
        } finally {
            destroy();
        }

    }

    static private void inicialize(String[] args) {
        String configFileName;
        if (args.length == 0) {
            configFileName = "iothomeagent.cfg.json";
        } else {
            configFileName = args[0];
        }

        config = ConfigLoader.getConfig(configFileName);
        microcontroller = new Microcontroller(config.getComPort());
    }

    static private void destroy() {
        if (microcontroller != null) {
            microcontroller.close();
        }
    }

    static private Measure getMeasure() {
        List<Float> powers = microcontroller.getPowers();

        Measure measure = new Measure();
        measure.getDevice().setIdDevice(config.getIdDevice());
        measure.setTime(new Date());
        measure.setStream0(powers.get(0));
        measure.setStream1(powers.get(1));
        measure.setStream2(powers.get(2));
        measure.setStream3(powers.get(3));
        
        return measure;
    }

    static private void saveMeasure(Measure measure) {
        Storage storage = new Storage(config.getMeasuresFileName());
        List<Measure> measures = storage.get();
        measures.add(measure);
        storage.save(measures);
    }

    static private void sendMeasures() {
        try {
            Storage storage = new Storage(config.getMeasuresFileName());
            Http http = new Http();
            URL url = new URL(config.getUrl());
            List<Measure> measures;
            do {
                measures = storage.get();
                if (measures.size() > 0) {
                    Measure measure = measures.get(0);
                    http.post(url, measure);
                    measures.remove(measure);
                    storage.save(measures);
                }

            } while (measures.size() != 0);

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
