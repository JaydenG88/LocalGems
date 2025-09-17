package com.localgems.localgems_backend.service.external;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;

import com.localgems.localgems_backend.dto.GooglePlacesDTO;

@Service
public class GoogleMapsService {
    private static final String BASE_URL = "https://places.googleapis.com/v1/places:searchText?key=";

    // Regex pattern to validate address format (street address, city, state)
    public static final String ADDRESS_REGEX = 
    "^\\s*([0-9]+\\s+[A-Za-z0-9\\s\\-\\.,#&']+?|[A-Za-z0-9\\s\\-\\.,#&']+?)\\s*," +  // Street address
    "\\s*([A-Za-z\\s\\-\\.]+?),?" +                                                 // City 
    "\\s*([A-Za-z]{2}|[A-Za-z\\s\\-\\.]+?)\\s*" +                                   // State/Province
    "(\\d{5}(-\\d{4})?)?\\s*$"; 

    @Value("${google.api.key}")
    private  String apiKey;
    private final RestTemplate restTemplate = new RestTemplate();

    public GooglePlacesDTO getPlaceDetailsFromAddress(String address) {
        if(!isValidAddressFormat(address)) {
            throw new IllegalArgumentException("Invalid address format. Please provide a valid street address, city, and state.");
        }

        String url = BASE_URL + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Goog-FieldMask",
            "places.id,places.displayName,places.formattedAddress,places.location," +
            "places.addressComponents,places.types,places.photos,places.rating," +
            "places.userRatingCount,places.editorialSummary,places.priceLevel,places.reviews," +
            "places.businessStatus,places.internationalPhoneNumber,places.websiteUri," +
            "places.googleMapsUri,places.primaryTypeDisplayName,places.currentOpeningHours," +
            "places.regularOpeningHours"
        );
        Map<String, Object> body = new HashMap<>();
        body.put("textQuery", address);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<HashMap> response = restTemplate.postForEntity(url, request, HashMap.class);

        System.out.println(response.getBody());
        return convertResponseToDTO(response.getBody());
    }

  private GooglePlacesDTO convertResponseToDTO(Map<String, Object> response) {
    List<Map<String, Object>> places = (List<Map<String, Object>>) response.get("places");
    if (places == null || places.isEmpty()) {
        return null;
    }
    
    Map<String, Object> place = places.get(0);
    GooglePlacesDTO dto = new GooglePlacesDTO();
    
    dto.setPlaceId((String) place.get("id"));
    dto.setAddress((String) place.get("formattedAddress"));
    
    Map<String, Double> location = (Map<String, Double>) place.get("location");
    if (location != null) {
        dto.setLatitude(location.get("latitude"));
        dto.setLongitude(location.get("longitude"));
    }
    
    Map<String, String> displayName = (Map<String, String>) place.get("displayName");
    if (displayName != null) {
        dto.setName(displayName.get("text"));
    }
    
    List<Map<String, Object>> addressComponents = 
        (List<Map<String, Object>>) place.get("addressComponents");
    
    if (addressComponents != null) {
        for (Map<String, Object> component : addressComponents) {
            List<String> types = (List<String>) component.get("types");
            if (types.contains("locality")) {
                dto.setCity((String) component.get("longText"));
            } else if (types.contains("administrative_area_level_1")) {
                dto.setState((String) component.get("shortText"));
            }
        }
    }
    
    List<Map<String, Object>> photos = (List<Map<String, Object>>) place.get("photos");
    if (photos != null && !photos.isEmpty()) {
        dto.setPhotoReference((String) photos.get(0).get("name"));
    }
    
    return dto;
    }

    private boolean isValidAddressFormat(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        
        return address.matches(ADDRESS_REGEX);
    }

    public static void main(String[] args) {
        // Example usage
        GoogleMapsService service = new GoogleMapsService();
        GooglePlacesDTO test = service.getPlaceDetailsFromAddress("11226 189th Ct poop, CA" );
        System.out.println("Place ID: " + test.getPlaceId());
        System.out.println("Name: " + test.getName());
        System.out.println("Address: " + test.getAddress());
        System.out.println("Latitude: " + test.getLatitude());
        System.out.println("Longitude: " + test.getLongitude());
        System.out.println("City: " + test.getCity());
        System.out.println("State: " + test.getState());
        System.out.println("Photo Reference: " + test.getPhotoReference());
        System.out.println("Categories: " + test.getCategories());
        System.out.println("Rating: " + test.getRating());
        System.out.println("Editorial Summary: " + test.getEditorialSummary());
        System.out.println("Review Snippets: " + test.getReviewSnippets());
    }

}
