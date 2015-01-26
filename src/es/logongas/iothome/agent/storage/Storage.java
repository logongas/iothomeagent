/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.logongas.iothome.agent.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.logongas.iothome.modelo.Measure;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author logongas
 */
public class Storage {

    ObjectMapper objectMapper = new ObjectMapper();
    String fileName;

    public Storage(String fileName) {
        this.fileName = fileName;
    }

    public List<Measure> get() {
        List<Measure> measures;
        InputStream inputStream = null;
        try {

            inputStream = new BufferedInputStream(new FileInputStream(fileName));
            measures=(List<Measure>)objectMapper.readValue(inputStream, new TypeReference<ArrayList<Measure>>() {});
            
            if (measures==null) {
                measures=new ArrayList<Measure>();
            }
            
            return measures;
        } catch (FileNotFoundException ex) {
            //Si no está el fichero simplemente retornamos un nuevo array vacio
            return new ArrayList<Measure>();
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

    public void save(List<Measure> measures) {
        OutputStream outputStream = null;
        try {

            outputStream = new BufferedOutputStream(new FileOutputStream(fileName));
            objectMapper.writeValue(outputStream, measures);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Storage.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
