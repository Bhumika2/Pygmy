package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.CatalogResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class Catalog {

    Logger logger = LoggerFactory.getLogger("Pygmy");
    public List<CatalogResponse> searchTopic(String topic){
        List<CatalogResponse> catalogResponse = null;
        try {
            logger.info("calling catalog microservice");
            ObjectMapper objectMapper = new ObjectMapper();
            HttpClient client = HttpClient.newHttpClient();
            String restUrl = URLEncoder.encode(topic, StandardCharsets.UTF_8.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/queryBySubject/"+ restUrl))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Response::" + response);
            if (response.statusCode() != 200) {
                logger.info("Response::"+response.statusCode());
            }
            //System.out.println(response.body().toString());
            catalogResponse = objectMapper.readValue(response.body().toString(), new TypeReference<List<CatalogResponse>>(){});
            //System.out.println(catalogResponse);
        } catch (Exception e) {
            logger.info(String.valueOf(e.getStackTrace()));
        }
        return catalogResponse;
    }

    public CatalogResponse lookupBook(Integer bookNumber) {
        CatalogResponse catalogResponse = null;
        try {
            logger.info("calling catalog microservice");
            ObjectMapper objectMapper = new ObjectMapper();
            //String catalogReqStr = objectMapper.writeValueAsString(catalogRequest);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/queryByItem/"+bookNumber))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Response::" + response);
            if (response.statusCode() != 200) {
                logger.info("Response::" +response.statusCode());
            }
            //System.out.println(response.body().toString());
            catalogResponse = objectMapper.readValue(response.body().toString(), CatalogResponse.class);
        } catch (Exception e) {
            logger.info(String.valueOf(e.getStackTrace()));
        }
        return catalogResponse;
    }
}
