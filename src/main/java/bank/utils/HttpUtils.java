package bank.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static class HttpResult {

        public String body;
        public int statusCode;

        public HttpResult(String body, int statusCode) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

    }

    public static HttpResult get(URL url, String data) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("GET");

        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(data);
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer body = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            body.append(inputLine);
        }

        in.close();

        return new HttpResult(body.toString(), con.getResponseCode());
    }

    public static HttpResult post(URL url, String data) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("POST");

        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(data);
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer body = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            body.append(inputLine);
        }

        in.close();

        return new HttpResult(body.toString(), con.getResponseCode());
    }

    public static HttpResult put(URL url, String data) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("PUT");

        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(data);
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer body = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
           body.append(inputLine);
        }

        in.close();

        return new HttpResult(body.toString(), con.getResponseCode());
    }

    public static HttpResult delete(URL url, String data) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setRequestMethod("DELETE");

        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(data);
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer body = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            body.append(inputLine);
        }

        in.close();

        return new HttpResult(body.toString(), con.getResponseCode());
    }

}
