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
import java.util.Date;
import java.util.List;

/**
 *
 * @author logongas
 */
public class Main {

    static Microcontroller microcontroller = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        List<Measure> measures;

        String configFileName;
        if (args.length==0) {
            configFileName="iothomeagent.cfg.json";
        } else {
            configFileName=args[0];
        }
        
        Config config = ConfigLoader.getConfig(configFileName);

        List<Double> powers;
        try {
            microcontroller = new Microcontroller(config.getComPort());
            powers = microcontroller.getPowers();
        } finally {
            if (microcontroller != null) {
                microcontroller.close();
            }
        }
        
        Measure measure = new Measure();
        measure.getDevice().setIdDevice(config.getIdDevice());
        measure.setTime(new Date());
        measure.setStream0(powers.get(0));
        measure.setStream1(powers.get(0));
        measure.setStream2(powers.get(0));

        Storage storage = new Storage(config.getMeasuresFileName());
        measures = storage.get();
        measures.add(measure);
        storage.save(measures);

        Http http = new Http();
        URL url = new URL(config.getUrl());
        do {
            measures = storage.get();
            if (measures.size() > 0) {
                measure = measures.get(0);
                http.post(url, measure);
                measures.remove(measure);
                storage.save(measures);
            }

        } while (measures.size() != 0);
    }
}
