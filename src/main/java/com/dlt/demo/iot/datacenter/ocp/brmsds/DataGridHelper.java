package com.dlt.demo.iot.datacenter.ocp.brmsds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataGridHelper {

    public DataGridHelper() {
        super();
    }

    public String getMethod(final String dgURL) throws IOException {
        System.out.println("Executing get method for " + dgURL);

        URL url = new URL(dgURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setDoOutput(true);

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        connection.connect();

        String line = new String();
        StringBuilder response = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line + '\n');
        }
        bufferedReader.close();

        System.out.println("Executing get method returned value: " + response.toString());
        System.out.println("----------------------------------------");
        System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
        System.out.println("----------------------------------------");

        connection.disconnect();

        return response.toString();
    }

    public void putMethod(final String dgURL, final String value) throws IOException {
        System.out.println(String.format("Executing put method of value: <%s> for %s", value, dgURL));

        URL url = new URL(dgURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setDoOutput(true);

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

        connection.connect();

        outputStreamWriter.write(value);
        outputStreamWriter.flush();
        outputStreamWriter.close();

        System.out.println("----------------------------------------");
        System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
        System.out.println("----------------------------------------");

        connection.disconnect();
    }

    @Override
    public String toString() {
        return "DataGridHelper []";
    }

}
