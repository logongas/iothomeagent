/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.logongas.iothome.agent;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.logongas.iothome.agent.storage.Storage;
import es.logongas.iothome.modelo.Measure;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author logongas
 */
public class ConfigLoader {

    public static Config getConfig(String fileName) {
        Config config;
        InputStream inputStream = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            config = (Config) objectMapper.readValue(inputStream, Config.class);

            return config;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
