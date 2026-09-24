import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class additionalObtainSeed {
    public static long byLaserQuantumFluctuation() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://qrng.anu.edu.au/API/jsonI.php?length=1&type=uint16")).GET().build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();

        if (!body.contains("\"success\":true")) {
            throw new RuntimeException("QRNG request failed: " + body);
        }

        Matcher matcher = Pattern.compile("\"data\":\\[(.*?)\\]").matcher(body);

        if (matcher.find()) {
            return (long) Integer.parseInt(matcher.group(1).trim());
        } else {
            throw new RuntimeException("Could not parse data from response: " + body);
        }
    }
}
