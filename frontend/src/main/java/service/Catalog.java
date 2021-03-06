package service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import com.google.inject.Inject;
import ninja.utils.NinjaProperties;

public class Catalog {

    Logger logger = LoggerFactory.getLogger("Pygmy");
    public Catalog(NinjaProperties ninjaProperties){
        this.ninjaProperties = ninjaProperties;
    }
    /**
     * searchTopic makes a http get request to catalog server to get the books information on given topic.
     * It is invoked from application controller and returns book information response from catalog server
     */
    @Inject
    NinjaProperties ninjaProperties;
    public List<CatalogResponse> searchTopic(String topic) {
        List<CatalogResponse> catalogResponse = null;
        try {
            logger.info("Calling Catalog microservice");
            ObjectMapper objectMapper = new ObjectMapper();
            HttpClient client = HttpClient.newHttpClient();
            String serverName = ninjaProperties.get("catalogHost")+":"+ninjaProperties.get("catalogPort");
            String restUrl = URLEncoder.encode(topic, StandardCharsets.UTF_8.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://"+serverName+"/queryBySubject/" + restUrl))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.info("Non 200 response code received from catalog server: " + response.statusCode());
            }
            catalogResponse = objectMapper.readValue(response.body().toString(), new TypeReference<List<CatalogResponse>>() {
            });
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(catalogResponse);
            logger.info("Response for search request: " + json);
        } catch (Exception e) {
            logger.info(String.valueOf(e.getStackTrace()));
        }
        return catalogResponse;
    }

    /**
     * lookupBook makes a http get request to catalog server to get the book information.
     * It is invoked from application controller and returns book information response from catalog server
     */
    public CatalogResponse lookupBook(Integer bookNumber) {
        CatalogResponse catalogResponse = null;
        try {
            logger.info("Calling Catalog microservice");
            ObjectMapper objectMapper = new ObjectMapper();
            //String catalogReqStr = objectMapper.writeValueAsString(catalogRequest);
            HttpClient client = HttpClient.newHttpClient();
            String serverName = ninjaProperties.get("catalogHost")+":"+ninjaProperties.get("catalogPort");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://"+serverName+"/queryByItem/" + bookNumber))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.info("Non 200 response code received from catalog server: " + response.statusCode());
            }
            catalogResponse = objectMapper.readValue(response.body().toString(), CatalogResponse.class);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(catalogResponse);
            logger.info("Response for lookup request: " + json);
        } catch (Exception e) {
            logger.info(String.valueOf(e.getStackTrace()));
        }
        return catalogResponse;
    }
}
