import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class HttpHelper {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String baseURL = "https://brainy-fatigues-toad.cyclic.app/api";

    public static <T> String objectToJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    public static <T> Boolean Post(String endpoint, T data) {
        Boolean ret = false;
        try {
            String jsonData = objectToJson(data);
            
            URL apiUrl = new URL(baseURL + endpoint);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(jsonData);
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            BufferedReader reader;
            String line;
            StringBuilder response = new StringBuilder();

            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                ret = true;
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            if (!(responseCode >= 200 && responseCode < 300)) System.out.println("Response: " + response.toString());

            connection.disconnect();

            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return ret;
        }
    }
}
