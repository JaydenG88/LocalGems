package com.localgems.localgems_backend.service.external;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleMapsService {
    private static final String BASE_URL = "https://places.googleapis.com/v1/places:searchText?key=";

    @Value("${google.api.key}")
    private  String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, String> getPlaceDetailsFromAddress(String address) {
        String url = BASE_URL + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Goog-FieldMask", "places.displayName,places.id,places.formattedAddress");

        Map<String, Object> body = new HashMap<>();
        body.put("textQuery", address);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return response.getBody();
    }
    public static void main(String[] args) {
        GoogleMapsService service = new GoogleMapsService();
        Map<String, String> result = service.getPlaceDetailsFromAddress("1600 Amphitheatre Parkway, Mountain View, CA");
        System.out.println(result);
    }
}
