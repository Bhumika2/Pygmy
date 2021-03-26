package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.CatalogResponse;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class Catalog {

    public List<CatalogResponse> searchTopic(String topic){
        List<CatalogResponse> catalogResponse = null;
        try {
            System.out.println("calling catalog microservice");
            ObjectMapper objectMapper = new ObjectMapper();
            HttpClient client = HttpClient.newHttpClient();
            String restUrl = URLEncoder.encode(topic, StandardCharsets.UTF_8.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8085/queryBySubject/"+ restUrl))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response::" + response);
            if (response.statusCode() != 200) {
                System.out.println(response.statusCode());
            }
            //System.out.println(response.body().toString());
            catalogResponse = objectMapper.readValue(response.body().toString(), new TypeReference<List<CatalogResponse>>(){});
            //System.out.println(catalogResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return catalogResponse;
    }

    public CatalogResponse lookupBook(Integer bookNumber) {
        CatalogResponse catalogResponse = null;
        try {
            System.out.println("calling catalog microservice");
            ObjectMapper objectMapper = new ObjectMapper();
            //String catalogReqStr = objectMapper.writeValueAsString(catalogRequest);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8085/queryByItem/"+bookNumber))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response::" + response);
            if (response.statusCode() != 200) {
                System.out.println(response.statusCode());
            }
            //System.out.println(response.body().toString());
            catalogResponse = objectMapper.readValue(response.body().toString(), CatalogResponse.class);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return catalogResponse;
    }
}
