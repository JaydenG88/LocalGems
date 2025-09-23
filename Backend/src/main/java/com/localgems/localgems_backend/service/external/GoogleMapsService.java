package com.localgems.localgems_backend.service.external;
import java.util.ArrayList;
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
    "^([A-Za-z0-9\\s\\-\\.,#&'()]+)," +                   // Business name
    "\\s*(?:([A-Za-z0-9\\s\\-\\.,#&'()]+),)?" +           // Optional address
    "\\s*([A-Za-z\\s\\-\\.]+)," +                         // City
    "\\s*([A-Za-z\\s\\.]{2,})\\s*$";                      // State (2+ characters)


    @Value("${google.api.key}")
    private  String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();


    public GooglePlacesDTO getPlaceDetailsBySearch(String searchQuery) {
        String placeId = getPlaceIdFromSearch(searchQuery);
        if (placeId != null) {
            Map<String, Object> placeDetails = getPlaceDetailsById(placeId);
            return mapToGooglePlacesDTO(placeDetails);
            
        } else {
            throw new RuntimeException("Place ID not found for the given search query");
        }
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
        
        // Basic fields that should always be present
        dto.setPlaceId((String) placeDetails.get("id"));
        
        // Handle displayName
        Map<String, Object> displayName = (Map<String, Object>) placeDetails.get("displayName");
        if (displayName != null) {
            dto.setName((String) displayName.get("text"));
        }
        
        // Handle address components - this is more complex as it involves nested structures
        List<Map<String, Object>> addressComponents = (List<Map<String, Object>>) placeDetails.get("addressComponents");
        if (addressComponents != null && addressComponents.size() >= 2) {
            String address = "";
            if (addressComponents.size() > 0) {
                address += (String) addressComponents.get(0).get("longText");
            }
            if (addressComponents.size() > 1) {
                address += " " + (String) addressComponents.get(1).get("longText");
            }
            dto.setAddress(address);
            
            // City and state - be careful with index access
            if (addressComponents.size() > 4) {
                dto.setCity((String) addressComponents.get(4).get("longText"));
            }
            if (addressComponents.size() > 6) {
                dto.setState((String) addressComponents.get(6).get("shortText"));
            }
        }
        
        // Handle location
        Map<String, Object> location = (Map<String, Object>) placeDetails.get("location");
        if (location != null) {
            dto.setLatitude((Double) location.get("latitude"));
            dto.setLongitude((Double) location.get("longitude"));
        }
        
        // Handle categories, rating and total ratings
        dto.setCategories((List<String>) placeDetails.get("types"));
        dto.setRating((Double) placeDetails.get("rating"));
        dto.setTotalRatings((Integer) placeDetails.get("userRatingCount"));
        
        // Handle potential null editorialSummary
        Map<String, String> editorialSummary = (Map<String, String>) placeDetails.get("editorialSummary");
        if (editorialSummary != null) {
            dto.setEditorialSummary(editorialSummary.get("text"));
        } else {
            dto.setEditorialSummary(null);
        }
        
        // Handle websiteUri which might be null
        dto.setWebsite((String) placeDetails.get("websiteUri"));

        // Handle potential null reviews
        List<String> reviewText = new ArrayList<>();
        List<Map<String, Object>> reviewsRaw = (List<Map<String, Object>>) placeDetails.get("reviews");
        
        if (reviewsRaw != null) {
            for (Map<String, Object> review : reviewsRaw) {
                Map<String, String> textMap = (Map<String, String>) review.get("text");
                if (textMap != null) {
                    reviewText.add(textMap.get("text"));
                }
            }
        }
        
        dto.setReviewSnippets(reviewText);

        return dto;
    }



}


