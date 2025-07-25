import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class VideoApiClient {

    public static void loadVideo(String path) throws IOException {
        URL url = new URL("http://localhost:8000/load");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String data = "path=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream is = conn.getInputStream()) {
            System.out.println(new String(is.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    public static void playVideo() throws IOException {
        URL url = new URL("http://localhost:8000/play");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.getInputStream().close();
    }

    public static void pauseVideo() throws IOException {
        URL url = new URL("http://localhost:8000/pause");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.getInputStream().close();
    }

    public static int getTimestampMs() throws IOException {
        URL url = new URL("http://localhost:8000/timestamp");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String json = reader.readLine();
            JSONObject obj = new JSONObject(json);
            return obj.getInt("timestamp_ms");
        }
    }
}
