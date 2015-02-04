/*
 * 
 * Microcontroller 18-feb-2009
 * 
 * Easy Layered Framework (ELF)
 * 
 * Copyright 2005-2009 Lorenzo Gonzolez Gascon
 * http://elfframework.sourceforge.net
 * mailto: logongas@users.sourceforge.net
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA02111-1307USA
 * 
 * Disclaimer:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package es.logongas.iothome.agent.arduino;


import gnu.io.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <a href="mailto:logongas@users.sourceforge.net">Lorenzo Gonzalez</a>
 */
public class Microcontroller implements SerialPortEventListener {


    static String COMMAND_SCT_013_READ="READ_WATT";
    
    static private int timeout = 20000;
    static private String appName = "Microcontroller";
    
    private String _portName;
    private SerialPort _serialPort=null;
    
    private InputStream _inputStream;
    private OutputStream _outputStream;






    /**
     * Crea una nueva instancia de la clase
     * @param portName Puerto serie donde se encuentra conectado el movil.
     */
    public Microcontroller(String portName) {

        inicialize(portName);
        
        
    }
    
    public void close() {
        if (_serialPort!=null) {
            _serialPort.close();
        }
    }
    
    

    private void inicialize(String portName) {
        try {

            _portName = portName;

            _serialPort = (SerialPort) CommPortIdentifier.getPortIdentifier(_portName).open(appName, timeout);

            _outputStream =_serialPort.getOutputStream();
            _inputStream =_serialPort.getInputStream();
            _serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);

            _serialPort.setSerialPortParams(9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            _serialPort.addEventListener(this);

            _serialPort.notifyOnDataAvailable(true);
            Thread.sleep(2000);
            
            
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

   
    public List<Float> getPowers() {
        String command=COMMAND_SCT_013_READ + ";";
        
        sendCommand(command);
        
        int raw0=Integer.parseInt(readLine());
        float power0=Float.parseFloat(readLine()); 
        int raw1=Integer.parseInt(readLine());
        float power1=Float.parseFloat(readLine()); 
        int raw2=Integer.parseInt(readLine()); 
        float power2=Float.parseFloat(readLine()); 
        
        List<Float> powers=new ArrayList<Float>();
        powers.add(power0);
        powers.add(power1);
        powers.add(power2);
        powers.add(0.0F);
        
        if ((raw0>1000) ||(raw1>1000) || (raw2>1000)) {
            System.out.println(raw0 + " " + raw1 + " " + raw2);
            this.close();
            System.exit(1);
        }

        return powers;
        
    }
    


    
 

    
    
    /**
     * Manda un mensaje al microcontrolador
     * 
     * @param command Orden a enviar al microcontrolador
     * @return Respuesta del microcontrolador
     * @throws MicrocontrollerException Si hubo algun error al leer los datos del microcontrolador
     */
    public synchronized void sendCommand(String command)  {
        try {
            _outputStream.write((command).getBytes(Charset.forName("ISO-8859-15")));
            _outputStream.flush();
            
            String status=readLine();
            if (status.equalsIgnoreCase("OK")) {
                return;
            } else if (status.equalsIgnoreCase("ERROR")) {
                throw new RuntimeException(readLine());
            } else {
                throw new RuntimeException("Respuesta inesperada del microcontrolador:"+status); 
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


    }

    public String readLine() {
        try {
            int size=2048;
            int len=size;
            char[] buffer=new char[size];
            for(int i=0;i<size;i++) {
                int data;
                while ((data=_inputStream.read())==-1){};

                buffer[i]=(char)data;
                
                if (i>=1) {
                    if ((buffer[i]==(int)'\n') && (buffer[i-1]==(int)'\r')) {
                        len=i-1;
                        break;
                    }
                    if ((buffer[i]==(int)'\r') && (buffer[i-1]==(int)'\n')) {
                        len=i-1;
                        break;
                    }                    
                }
                if (buffer[i]==(int)'\n')  {
                    len=i;
                    break;
                }               
            }
            
            String response=new StringBuffer().append(buffer, 0, len).toString();
            return response;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }        
    }

    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType()==SerialPortEvent.DATA_AVAILABLE) {
        } else if (serialPortEvent.getEventType()==SerialPortEvent.OUTPUT_BUFFER_EMPTY) {
        } else {
        }
    }


}
