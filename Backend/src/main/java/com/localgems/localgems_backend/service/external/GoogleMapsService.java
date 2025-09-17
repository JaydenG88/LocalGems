package com.localgems.localgems_backend.service.external;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleMapsService {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${google.api.key}")
    private  String apiKey;

    @SuppressWarnings("unchecked")
    public Map<String, String> getPlaceDetailsFromAddress(String address) {
        String url = BASE_URL + "?query=" + address.replace(" ", "+") + "&key=" + apiKey;
        Map<String, String> params = new HashMap<>();
        params.put("url", url);
        params.put("apiKey", apiKey);

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class, params);

        return (Map<String, String>) response.getBody();
    }
}
