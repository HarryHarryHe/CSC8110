import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

public class RequestClient {
    private static final String TARGET = System.getenv("TARGET");
    private static final int FREQUENCY = Integer.parseInt(System.getenv("FREQUENCY"));

    private static int timeoutCount = 0;

    public static void main(String[] args) {
        while (true) {
            try {
                Instant start = Instant.now();
                URL url = new URL(TARGET);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                //1s
                connection.setConnectTimeout(1000);
                connection.setRequestMethod("GET");
                int responseCode = connection.getResponseCode();
                Instant end = Instant.now();
                long responseTime = Duration.between(start, end).toMillis();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    if (responseTime - 900 > 0) {
                        timeoutCount++;
                        System.out.println("Request timeout: " + responseTime + " Total timeouts: " + timeoutCount);
                    } else {
                        System.out.println("Response Time: " + responseTime + " ms");
                    }
                } else {
                    System.out.println("Request failed with response code: " + responseCode);
                }

                Thread.sleep(1000 / FREQUENCY);
            } catch (Exception e) {
                System.out.println("Request failed with exception: " + e.getMessage());
            }
        }
    }
}
