package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Book;
import models.BuyRequest;
import models.BuyResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BuyItem {
    Logger logger = LoggerFactory.getLogger("Pygmy");
    public BuyResponse buy(BuyRequest buyObj) {
        Integer bookNumber = buyObj.getBookNumber();
        Book book = checkBookAvailability(bookNumber);
        String message;
        if (book != null && book.getCount() > 0) {
            boolean status = initiateBuy(bookNumber);
            message = "Buy failed for book - " + book.getBookName();
            if (status) {
                message = "Successfully bought book - " + book.getBookName();
            }
        } else {
            message = "Insufficient quantity of book - " + book.getBookName();
            restockBook(bookNumber);
        }
        BuyResponse buyResponse = new BuyResponse();
        buyResponse.setBookNumber(bookNumber);
        buyResponse.setMessage(message);
        return buyResponse;
    }

    public Book checkBookAvailability(Integer bookNumber) {
        Book book = null;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/queryByItem/" + bookNumber))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                //TODO: error
            }
            ObjectMapper mapper = new ObjectMapper();
            book = mapper.readValue(response.body().toString(), Book.class);
        } catch (Exception e) {
            //TODO: error
        }
        return book;
    }

    public boolean initiateBuy(Integer bookNumber) {
        logger.info("Initiating buy request for book: " + bookNumber);
        boolean buyStatus = false;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/update/" + bookNumber + "/buy"))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                //TODO: error
            }
            ObjectMapper mapper = new ObjectMapper();
            BuyResponse buyResponse = mapper.readValue(response.body().toString(), BuyResponse.class);
            if (buyResponse.getMessage().equals("success")) {
                buyStatus = true;
            }
        } catch (Exception e) {
            //TODO: error
        }
        return buyStatus;
    }

    public void restockBook(Integer bookNumber) {
        logger.info("Initiating restock request for book: " + bookNumber);
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/update/" + bookNumber + "/restock"))
                    .timeout(Duration.ofMinutes(1))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                //TODO: error
            }
        } catch (Exception e) {
            //TODO: error
        }
    }
}
