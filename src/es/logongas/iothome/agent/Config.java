/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.logongas.iothome.agent;

/**
 *
 * @author logongas
 */
public class Config {
    

    
    private String comPort;
    private String url;
    private int idDevice;
    private String measuresFileName;
    
    /**
     * @return the comPort
     */
    public String getComPort() {
        return comPort;
    }

    /**
     * @param comPort the comPort to set
     */
    public void setComPort(String comPort) {
        this.comPort = comPort;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the idDevice
     */
    public int getIdDevice() {
        return idDevice;
    }

    /**
     * @param idDevice the idDevice to set
     */
    public void setIdDevice(int idDevice) {
        this.idDevice = idDevice;
    }

    /**
     * @return the measuresFileName
     */
    public String getMeasuresFileName() {
        return measuresFileName;
    }

    /**
     * @param measuresFileName the measuresFileName to set
     */
    public void setMeasuresFileName(String measuresFileName) {
        this.measuresFileName = measuresFileName;
    }
    

    
}
