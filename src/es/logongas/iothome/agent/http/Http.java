/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.logongas.iothome.agent.http;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author logongas
 */
public class Http {

    private final ObjectMapper objectMapper;

    public Http() {
        this.objectMapper = new ObjectMapper();

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        SimpleModule module = new SimpleModule();
        module.addSerializer(java.util.Date.class, new DateSerializer());
        objectMapper.registerModule(module);
    }

    public Object post(URL url, Object data) {
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(url.toURI());

            StringEntity input = new StringEntity(objectMapper.writeValueAsString(data));
            input.setContentType("application/json");
            postRequest.setEntity(input);

            HttpResponse response = httpClient.execute(postRequest);
            String output = inputStreamToString(response.getEntity().getContent());

            if (response.getStatusLine().getStatusCode() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode() + "\n" + output);
            }

            httpClient.getConnectionManager().shutdown();

            return objectMapper.readValue(output, data.getClass());

        } catch (Exception ex) {

            throw new RuntimeException(ex);

        }
    }

    private String inputStreamToString(InputStream inputStream) {
        try {
            String line;
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (null != (line = reader.readLine())) {
                sb.append(line);
                sb.append('\n');
            }
            return sb.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
