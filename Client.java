import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Random;

public class Client {
    private static final String[] ACTIONS = { "lookup", "search", "buy" };
    private static final String[] TOPICS = { "distributed systems", "graduate school"};

    public static void main(String[] args) {
        try {
            while (true) {
                String action = getRandomAction();
                System.out.println("Action: " + action);
                if (action.equals("lookup")) {
                    int id = getRandomNumber();
                    System.out.println("Requesting details of book: " + id);
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/lookup/" + id))
                                .timeout(Duration.ofMinutes(1))
                                .header("Content-Type", "application/json")
                                .GET()
                                .build();
                        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        System.out.println("Response::" + response);
                        if (response.statusCode() != 200) {
                            System.out.println(response.statusCode());
                        }
                        System.out.println(response.body().toString());
                    } catch (Exception e) {

                    }
                } else if (action.equals("search")) {
                    String topic = getRandomTopic();
                    System.out.println("Requesting book details corresponding to topic: " + topic);
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        String restUrl = URLEncoder.encode(topic, StandardCharsets.UTF_8.toString());
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/search/" + restUrl))
                                .timeout(Duration.ofMinutes(1))
                                .header("Content-Type", "application/json")
                                .GET()
                                .build();
                        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        System.out.println("Response::" + response);
                        if (response.statusCode() != 200) {
                            System.out.println(response.statusCode());
                        }
                        System.out.println(response.body().toString());
                    } catch (Exception e) {

                    }
                } else if (action.equals("buy")) {
                    int id = getRandomNumber();
                    System.out.println("Buy Request for book: " + id);
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/buy/" + id))
                                .timeout(Duration.ofMinutes(1))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build();
                        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
                        System.out.println("Response::" + response);
                        if (response.statusCode() != 200) {
                            System.out.println(response.statusCode());
                        }
                        System.out.println(response.body().toString());
                    } catch (Exception e) {

                    }
                }
                Thread.sleep(1000);
            }
        }catch(Exception e){

        }

    }

    public static String getRandomAction() {
        Random random = new Random();
        int randomNum = random.nextInt(ACTIONS.length);
        return ACTIONS[randomNum];
    }
    public static int getRandomNumber() {
        Random random = new Random();
        int randomNum = random.nextInt(4)+1;
        return randomNum;
    }
    public static String getRandomTopic() {
        Random random = new Random();
        int randomNum = random.nextInt(TOPICS.length);
        return TOPICS[randomNum];
    }
}