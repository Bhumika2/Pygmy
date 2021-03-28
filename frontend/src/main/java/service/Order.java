package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.OrderRequest;
import models.OrderResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Order {

    public OrderResponse buyBook(OrderRequest orderReq) {
        OrderResponse orderResponse = null;
        try {
            System.out.println("calling order microservice");
            ObjectMapper objectMapper = new ObjectMapper();
            String orderReqStr = objectMapper.writeValueAsString(orderReq);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8082/buy"))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(orderReqStr))
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response::" + response);
            if (response.statusCode() != 200) {
                System.out.println(response.statusCode());
            }
            orderResponse = objectMapper.readValue(response.body().toString(), OrderResponse.class);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return orderResponse;
    }
}
