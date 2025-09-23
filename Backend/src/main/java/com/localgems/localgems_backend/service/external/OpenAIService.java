package com.localgems.localgems_backend.service.external;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localgems.localgems_backend.dto.requestDTO.BusinessRequestDTO;
import com.localgems.localgems_backend.dto.externalDTO.GooglePlacesDTO;
import com.localgems.localgems_backend.service.CategoryService;
import com.localgems.localgems_backend.service.CityService;
import com.localgems.localgems_backend.dto.responseDTO.CategoryResponseDTO;
import com.localgems.localgems_backend.dto.responseDTO.CityResponseDTO;

import java.util.*;


@Service
public class OpenAIService {
    
    private final CategoryService categoryService;
    private final CityService cityService;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private String openaiApiUrl = "https://api.openai.com/v1/chat/completions";
    private final RestTemplate restTemplate = new RestTemplate();


    public OpenAIService(CategoryService categoryService, CityService cityService) {
        this.categoryService = categoryService;
        this.cityService = cityService;
    }

    public BusinessRequestDTO generateBusinessRequestDTO(GooglePlacesDTO place) {
        if (place == null) {
            throw new IllegalArgumentException("Google Places data cannot be null");
        }

        if (!validBusiness(place)) {
            throw new IllegalArgumentException("Business does not meet the required criteria.");
        }

        List<CityResponseDTO> cities = cityService.getAllCities();
        if (cities.isEmpty()) {
            throw new IllegalStateException("No cities available in the database. Please add cities first.");
        }

        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        if (categories.isEmpty()) {
            throw new IllegalStateException("No categories available in the database. Please add categories first.");
        }

        String prompt = buildPrompt(place, cities, categories);
        Map<String, Object> response;
        try {
            response = callOpenAIAPI(prompt);
        } catch (Exception e) {
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to communicate with OpenAI API", e);
        }

        Map<String, Object> result;
        try {
            result = parseOpenAIResponse(response);
        } catch (Exception e) {
            System.err.println("Error parsing OpenAI response: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse OpenAI API response", e);
        }

        // Validate result has required fields
        if (result == null || !result.containsKey("name") || !result.containsKey("address")) {
            throw new IllegalStateException("OpenAI response missing basic required fields: " + result);
        }
        
        // Check if either cityId or both cityName and cityState are present
        boolean hasCityId = result.containsKey("cityId");
        boolean hasCityDetails = result.containsKey("cityName") && result.containsKey("cityState");
        
        if (!hasCityId && !hasCityDetails) {
            // If no city information, try to extract from place
            if (place.getCity() != null && place.getState() != null) {
                result.put("cityName", place.getCity());
                result.put("cityState", place.getState());
            } else {
                throw new IllegalStateException("OpenAI response missing city information and no city info in place data: " + result);
            }
        }
        
        if (!result.containsKey("categoryIds")) {
            throw new IllegalStateException("OpenAI response missing category information: " + result);
        }

        BusinessRequestDTO businessRequestDTO = new BusinessRequestDTO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            businessRequestDTO = objectMapper.convertValue(result, BusinessRequestDTO.class);
            
            // Additional validation
            if (businessRequestDTO.getName() == null || businessRequestDTO.getName().length() < 3) {
                throw new IllegalStateException("Business name is too short or null");
            }
            if (businessRequestDTO.getCategoryIds() == null || businessRequestDTO.getCategoryIds().isEmpty()) {
                throw new IllegalStateException("Business must have at least one category");
            }
            
            return businessRequestDTO;
        } catch (Exception e) {
            System.err.println("Error converting OpenAI response to BusinessRequestDTO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to convert OpenAI response to BusinessRequestDTO", e);
        }
    }

    private Boolean validBusiness(GooglePlacesDTO place) {
        List<CityResponseDTO> cities = cityService.getAllCities();

        String prompt = validateBusinessPrompt(place, cities, categoryService.getAllCategories());
        Map<String, Object> response = callOpenAIAPI(prompt);
        Map<String, Object> result = parseOpenAIResponse(response);

        if (result.containsKey("isValid")) {
            return (Boolean) result.get("isValid");
        }
        
        return false;
    }

    private Map<String, Object> parseOpenAIResponse(Map<String, Object> response) {
        if (response == null || !response.containsKey("choices")) {
            throw new IllegalArgumentException("Invalid response from OpenAI API: " + (response != null ? response.toString() : "null"));
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices == null || choices.isEmpty() || !choices.get(0).containsKey("message")) {
            throw new IllegalArgumentException("No choices found in OpenAI API response: " + response.toString());
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (!message.containsKey("content")) {
            throw new IllegalArgumentException("No content found in OpenAI API message: " + message.toString());
        }

        String content = (String) message.get("content");
        
        // Log the content for debugging
        System.out.println("OpenAI API Response Content: " + content);

        try {
            // Strip any non-JSON text that might be surrounding the JSON
            content = content.trim();
            if (content.startsWith("```json")) {
                content = content.substring(7);
            }
            if (content.endsWith("```")) {
                content = content.substring(0, content.length() - 3);
            }
            content = content.trim();
            
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            System.err.println("Failed to parse JSON content: " + content);
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to parse JSON content from OpenAI API response", e);
        }
    }

    private Map<String, Object> callOpenAIAPI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        // Using gpt-3.5-turbo as a fallback if gpt-4 is not available
        requestBody.put("model", "gpt-3.5-turbo"); 
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        // Add system message to improve results
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "You are a helpful assistant that generates valid JSON data for a business database application. " +
                                   "Always return valid JSON that conforms exactly to the schema provided. " +
                                   "Make sure all required fields are present and formatted correctly.");
        messages.add(systemMessage);
        
        // Add user prompt
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 2000); // Increased to handle larger responses
        requestBody.put("temperature", 0.2); // Lower temperature for more consistent, predictable output
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Implement retry logic
        int maxRetries = 3;
        int retryCount = 0;
        Map<String, Object> response = null;
        
        while (retryCount < maxRetries) {
            try {
                response = restTemplate.postForObject(openaiApiUrl, request, Map.class);
                if (response != null && response.containsKey("choices")) {
                    return response;
                }
                
                // If response is invalid but no exception thrown, wait and retry
                System.out.println("Invalid OpenAI response, retrying... (Attempt " + (retryCount + 1) + "/" + maxRetries + ")");
                
            } catch (Exception e) {
                System.err.println("OpenAI API call failed: " + e.getMessage() + 
                                 " - Retrying... (Attempt " + (retryCount + 1) + "/" + maxRetries + ")");
            }
            
            // Wait before retrying (with exponential backoff)
            try {
                Thread.sleep(1000 * (long)Math.pow(2, retryCount));
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted while waiting to retry OpenAI API call", ie);
            }
            
            retryCount++;
        }
        
        if (response == null) {
            throw new RuntimeException("Failed to get valid response from OpenAI API after " + maxRetries + " attempts");
        }
        
        return response;
    }


    private String buildPrompt(GooglePlacesDTO place, List<CityResponseDTO> cities, List<CategoryResponseDTO> categories) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Given the following business details from Google Places, generate a BusinessRequestDTO in JSON format suitable for our application. ");
        prompt.append("Ensure the business name is between 3 and 100 characters, the address is complete, and the city and state match one of our supported cities. ");
        prompt.append("Also, categorize the business using our predefined categories. If no suitable category exists, use 'Other'.\n\n");
        prompt.append(place.toString()).append("\n\n");   
        prompt.append("\n");

        prompt.append("Predefined Categories (ID, Name):\n");
        for (CategoryResponseDTO category : categories) {
            prompt.append("- ID: ").append(category.getCategoryId()).append(", Name: ").append(category.getName()).append("\n");
        }

        prompt.append("Supported Cities (ID, Name, State):\n");
        for (CityResponseDTO city : cities) {
            prompt.append("- ID: ").append(city.getCityId()).append(", Name: ").append(city.getName()).append(", State: ").append(city.getState()).append("\n");
        }

        prompt.append("\n");

        prompt.append("- The schema for BusinessRequestDTO is as follows:\n");
        prompt.append("{\n");
        prompt.append("  \"googlePlaceId\": \"string (required)\",\n");
        prompt.append("  \"name\": \"string (3-100 characters, required)\",\n");
        prompt.append("  \"address\": \"string (required)\",\n");
        prompt.append("  \"cityId\": \"long (must match one of the supported cities by ID, optional if cityName and cityState are provided)\",\n");
        prompt.append("  \"cityName\": \"string (name of the city, required if cityId is not provided)\",\n");
        prompt.append("  \"cityState\": \"string (2-letter state code, required if cityId is not provided)\",\n");
        prompt.append("  \"categoryIds\": \"list of long (must match one of the predefined categories by ID, at least one required)\",\n");
        prompt.append("  \"description\": \"string (optional, max 1000 characters)\",\n");
        prompt.append("  \"website\": \"string (optional, valid URL format)\",\n");
        prompt.append("  \"latitude\": \"double (optional, valid latitude)\",\n");
        prompt.append("  \"longitude\": \"double (optional, valid longitude)\"\n");
        prompt.append("}\n\n");
        prompt.append("Write a new description for the business based on the place details.\n\n");
        prompt.append("Populate the categoryIds array with IDs from the Predefined Categories list. Match categories based on the business details. If none fit well, include the ID of 'Other'.\n\n");
        prompt.append("For the city information, you can either:\n");
        prompt.append("1. Set the cityId to match a city in our Supported Cities list, OR\n");
        prompt.append("2. Provide both cityName and cityState (this will create a new city if it doesn't exist)\n\n");
        prompt.append("If the city exists in our Supported Cities list, use its ID. Otherwise, provide the cityName and cityState.\n\n");

        prompt.append("Return only the JSON representation of the BusinessRequestDTO without any additional text or explanation.");

        return prompt.toString();
    }

    private String validateBusinessPrompt(GooglePlacesDTO place, List<CityResponseDTO> cities, List<CategoryResponseDTO> categories) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Given the following business details from Google Places, determine if the business meets the required criteria for inclusion in our application. ");
        prompt.append("The business name must be between 3 and 100 characters, the address must be complete, and the city and state must match one of our supported cities. ");
        prompt.append("The business must be a local establishment and not a residential address or non-business location.\n\n");
        prompt.append("The business should not be a national or international chain without unique local characteristics.");
        prompt.append("The business should be locally owned or have a significant local presence.\n\n");
        prompt.append("Business Details:\n");
        prompt.append(place.toString()).append("\n\n");   

        prompt.append("Supported Cities:\n");
        for (CityResponseDTO city : cities) {
            prompt.append("- ").append(city.getName()).append(", ").append(city.getState()).append("\n");
        }   

        prompt.append("\n");
        prompt.append("Note: Even if the city is not in our supported cities list, the business can still be valid. We will automatically add new cities.\n\n");
        prompt.append("Response in a JSON format as follows:\n");
        prompt.append("{\n");
        prompt.append("  \"isValid\": \"boolean (true if the business meets all criteria, false otherwise)\",\n");
        prompt.append("  \"reason\": \"string (if isValid is false, provide a brief reason why the business does not meet the criteria; otherwise, return 'N/A')\"\n");
        prompt.append("}\n\n");

        return prompt.toString();
    }
    


}
