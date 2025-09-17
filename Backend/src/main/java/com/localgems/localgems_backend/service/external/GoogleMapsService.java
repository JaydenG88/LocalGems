package com.localgems.localgems_backend.service.external;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.localgems.localgems_backend.dto.externalDTO.GooglePlacesDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleMapsService {
    private static final String SEARCH_URL = "https://places.googleapis.com/v1/places:searchText?key=";
    private static final String PLACE_DETAILS_URL = "https://places.googleapis.com/v1/places/";

    // Regex pattern to validate address format (street address, city, state)
    public static final String ADDRESS_REGEX = 
    "^\\s*([0-9]+\\s+[A-Za-z0-9\\s\\-\\.,#&']+?|[A-Za-z0-9\\s\\-\\.,#&']+?)\\s*," +  // Street address
    "\\s*([A-Za-z\\s\\-\\.]+?),?" +                                                 // City 
    "\\s*([A-Za-z]{2}|[A-Za-z\\s\\-\\.]+?)\\s*" +                                   // State/Province
    "(\\d{5}(-\\d{4})?)?\\s*$"; 

    @Value("${google.api.key}")
    private  String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    public void getPlaceDetailsById(String placeId) {
        String url = PLACE_DETAILS_URL + placeId + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Goog-FieldMask", "id,displayName.text,formattedAddress,location.latitude,location.longitude,addressComponents,types,photos.name,rating,userRatingCount,priceLevel,editorialSummary.text,reviews.text.text" );

        
    }



    public String getPlaceIdFromAddress(String address) {
        if(!isValidAddressFormat(address)) {
            throw new IllegalArgumentException("Invalid address format. Please provide a valid street address, city, and state.");
        }

        String url = SEARCH_URL + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Goog-FieldMask","places.id");
        
        Map<String, Object> body = new HashMap<>();
        body.put("textQuery", address);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<HashMap> response = restTemplate.postForEntity(url, request, HashMap.class);
        
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() == null) {
            throw new RuntimeException("Failed to fetch place ID from Google Places API");

        }

        String placeId = getPlaceIdFromResponse(response);
        return placeId;
    }

    private String getPlaceIdFromResponse(ResponseEntity<HashMap> response) {
        Map<String, Object> bodyResponse = response.getBody();
        List<Map> places =  (List<Map>)bodyResponse.get("places");
        String placeId = (String) places.get(0).get("id");

        return placeId;
    }


    private boolean isValidAddressFormat(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        
        return address.matches(ADDRESS_REGEX);
    }

    public static void main(String[] args) {
        GoogleMapsService service = new GoogleMapsService();
        System.out.println(service.getPlaceIdFromAddress("1600 Amphitheatre Parkway, Mountain View, CA"));
    }

}
