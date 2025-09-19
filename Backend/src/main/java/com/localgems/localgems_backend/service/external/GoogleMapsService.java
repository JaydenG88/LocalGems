package com.localgems.localgems_backend.service.external;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.localgems.localgems_backend.dto.externalDTO.GooglePlacesDTO;

@Service
public class GoogleMapsService {
    private static final String SEARCH_URL = "https://places.googleapis.com/v1/places:searchText?key=";
    private static final String PLACE_DETAILS_URL = "https://places.googleapis.com/v1/places/"; 

    public static final String BUSINESS_SEARCH_REGEX = 
    "^([A-Za-z0-9\\s\\-\\.,#&'()]+)," +     // Business name
    "\\s*([A-Za-z\\s\\-\\.]+)," +           // City
    "\\s*([A-Za-z\\s\\.]{2,})\\s*$";        // State (2+ characters)


    @Value("${google.api.key}")
    private  String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();


    public GooglePlacesDTO getPlaceDetailsBySearch(String searchQuery) {
        String placeId = getPlaceIdFromSearch(searchQuery);


        return null;
    }

    private Map<String, Object> getPlaceDetailsById(String placeId) {
        String url = PLACE_DETAILS_URL + placeId + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", apiKey);
        headers.set("X-Goog-FieldMask",
            "id,displayName,formattedAddress,location,addressComponents,types,rating,userRatingCount"
            + ",editorialSummary,priceLevel,websiteUri"
            + ",reviews"
        );

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class, placeId);

        return response.getBody();
    }

    private String getPlaceIdFromSearch(String searchQuery) {
        if (!isValidSearchQuery(searchQuery)) {
            throw new IllegalArgumentException("Invalid search query format. Expected format: 'Business Name, City, State'");
        }

        String url = SEARCH_URL + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-FieldMask","places.id");
        
        Map<String, Object> body = new HashMap<>();
        body.put("textQuery", searchQuery);

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

    private Boolean isValidSearchQuery(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return false;
        }

        return searchQuery.matches(BUSINESS_SEARCH_REGEX);
    }

    private GooglePlacesDTO mapToGooglePlacesDTO(Map<String, Object> placeDetails) {
        GooglePlacesDTO dto = new GooglePlacesDTO();


        dto.setPlaceId((String) placeDetails.get("id"));
        dto.setName((String) ((Map<String, Object> )placeDetails.get("displayName")).get("text"));
        dto.setAddress((String) ((List<Map<String, Object>>)placeDetails.get("addressComponents")).get(0).get("longText"));
        dto.setLatitude((Double) ((Map<String, Object>)placeDetails.get("location")).get("latitude"));
        dto.setLongitude((Double) ((Map<String, Object>)placeDetails.get("location")).get("longitude"));
        dto.setCity((String) ((List<Map<String, Object>>)placeDetails.get("addressComponents")).get(4).get("longText"));
        dto.setState((String) ((List<Map<String, Object>>)placeDetails.get("addressComponents")).get(6).get("shortText"));
        dto.setCategories((List<String>) placeDetails.get("types"));

        return dto;
    }

    public static void main(String[] args) {
        GoogleMapsService service = new GoogleMapsService();
        String id = service.getPlaceIdFromSearch("Golden Age Collectables, Seattle, WA");


        Map<String, Object> test = service.getPlaceDetailsById(id);
        GooglePlacesDTO dto = service.mapToGooglePlacesDTO(test);
        System.out.println(dto.getPlaceId());
        System.out.println(dto.getName());
        System.out.println(dto.getAddress());
        System.out.println(dto.getLatitude());
        System.out.println(dto.getLongitude());
        System.out.println(dto.getCity());
        System.out.println(dto.getState());
        System.out.println(dto.getCategories());
    }

}
