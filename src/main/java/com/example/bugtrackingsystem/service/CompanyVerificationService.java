package com.example.bugtrackingsystem.service;

// ADD this line:
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompanyVerificationService {

    @Value("${companies.house.api.key}")
    private String apiKey;


    private static final String API_BASE = "https://api.company-information.service.gov.uk/company/";

    public boolean verifyCompanyNumber(String companyNumber) {
        try {
            URL url = new URL(API_BASE + companyNumber);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String basicAuth = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());
            connection.setRequestProperty("Authorization", basicAuth);

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode != 200) {
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    String errorResponse = new BufferedReader(new InputStreamReader(errorStream))
                            .lines().collect(Collectors.joining("\n"));
                    System.out.println("Error Response: " + errorResponse);
                }
            }

            return responseCode == 200;


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, String> fetchCompanyData(String companyNumber) {
        try {
            URL url = new URL(API_BASE + companyNumber);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String basicAuth = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());
            connection.setRequestProperty("Authorization", basicAuth);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                String json = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                        .lines().collect(Collectors.joining("\n"));
                JSONObject obj = new JSONObject(json);

                String name = obj.getString("company_name");
                String date = obj.optString("date_of_creation", "N/A");

                Map<String, String> data = new HashMap<>();
                data.put("name", name);
                data.put("date", date);
                return data;
            } else {
                System.out.println("Companies House API returned: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public String getOfficialCompanyName(String companyNumber) {
        try {
            URL url = new URL("https://api.company-information.service.gov.uk/company/" + companyNumber);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String basicAuth = "Basic " + Base64.getEncoder().encodeToString((apiKey + ":").getBytes());
            connection.setRequestProperty("Authorization", basicAuth);

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == 200) {
                String json = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                        .lines().collect(Collectors.joining("\n"));
                JSONObject obj = new JSONObject(json);
                return obj.getString("company_name");
            } else {
                InputStream errorStream = connection.getErrorStream();
                if (errorStream != null) {
                    String errorResponse = new BufferedReader(new InputStreamReader(errorStream))
                            .lines().collect(Collectors.joining("\n"));
                    System.out.println("Error Response: " + errorResponse);
                }
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}




