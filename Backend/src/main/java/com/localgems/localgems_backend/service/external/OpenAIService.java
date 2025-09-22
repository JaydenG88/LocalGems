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

        if (!validBusiness(place)) {
            throw new IllegalArgumentException("Business does not meet the required criteria.");
        }

        List<CityResponseDTO> cities = cityService.getAllCities();
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        String prompt = buildPrompt(place, cities, categories);
        Map<String, Object> response = callOpenAIAPI(prompt);
        Map<String, Object> result = parseOpenAIResponse(response);

        BusinessRequestDTO businessRequestDTO = new BusinessRequestDTO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            businessRequestDTO = objectMapper.convertValue(result, BusinessRequestDTO.class);
            return businessRequestDTO;
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return businessRequestDTO;
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
            throw new IllegalArgumentException("Invalid response from OpenAI API");
        }

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        if (choices.isEmpty() || !choices.get(0).containsKey("message")) {
            throw new IllegalArgumentException("No choices found in OpenAI API response");
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        if (!message.containsKey("content")) {
            throw new IllegalArgumentException("No content found in OpenAI API message");
        }

        String content = (String) message.get("content");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse JSON content from OpenAI API response", e);
        }
    }

    private Map<String, Object> callOpenAIAPI (String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + openaiApiKey);
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4.1-mini");
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);
        messages.add(userMessage);
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(openaiApiUrl, request, Map.class);

        return response;
    }


    private String buildPrompt(GooglePlacesDTO place, List<CityResponseDTO> cities, List<CategoryResponseDTO> categories) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Given the following business details from Google Places, generate a BusinessRequestDTO in JSON format suitable for our application. ");
        prompt.append("Ensure the business name is between 3 and 100 characters, the address is complete, and the city and state match one of our supported cities. ");
        prompt.append("Also, categorize the business using our predefined categories. If no suitable category exists, use 'Other'.\n\n");
        prompt.append(place.toString()).append("\n\n");   
        prompt.append("\n");

        prompt.append("Predefined Categories:\n");
        for (CategoryResponseDTO category : categories) {
            prompt.append("- ").append(category.getName()).append("\n");
        }

        prompt.append("Supported Cities:\n");
        for (CityResponseDTO city : cities) {
            prompt.append("- ").append(city.getName()).append(", ").append(city.getState()).append("\n");
        }

        prompt.append("\n");

        prompt.append("- The schema for BusinessRequestDTO is as follows:\n");
        prompt.append("{\n");
        prompt.append("  \"googlePlaceId\": \"string (required)\",\n");
        prompt.append("  \"name\": \"string (3-100 characters, required)\",\n");
        prompt.append("  \"address\": \"string (required)\",\n");
        prompt.append("  \"cityId\": \"long (must match one of the supported cities, required)\",\n");
        prompt.append("  \"categories\": \"list of long (must match one of the predefined categories, at least one required)\",\n");
        prompt.append("  \"description\": \"string (optional, max 1000 characters)\",\n");
        prompt.append("  \"website\": \"string (optional, valid URL format)\",\n");
        prompt.append("  \"latitude\": \"double (optional, valid latitude)\",\n");
        prompt.append("  \"longitude\": \"double (optional, valid longitude)\"\n");
        prompt.append("}\n\n");
        prompt.append("Write a new description for the business based on the place details.\n\n");
        prompt.append("Populate the categories column using the Predefined Categories. Categorize them based off the Business Details. If none fit well, choose 'Other':\n");


        prompt.append("- Other\n\n");

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
        prompt.append("Response in a JSON format as follows:\n");
        prompt.append("{\n");
        prompt.append("  \"isValid\": \"boolean (true if the business meets all criteria, false otherwise)\",\n");
        prompt.append("  \"reason\": \"string (if isValid is false, provide a brief reason why the business does not meet the criteria; otherwise, return 'N/A')\"\n");
        prompt.append("}\n\n");

        return prompt.toString();
    }
    


}
