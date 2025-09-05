package com.localgems.localgems_backend.service;
import org.springframework.stereotype.Service;
import com.localgems.localgems_backend.model.Category;
import com.localgems.localgems_backend.repository.CategoryRepository;   
import com.localgems.localgems_backend.mapper.CategoryMapper;
import com.localgems.localgems_backend.dto.CategoryRequestDTO;
import com.localgems.localgems_backend.dto.CategoryResponseDTO;
import java.util.*;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryService (CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory (CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryMapper.dtoToEntity(categoryRequestDTO);
        Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryResponseDTO> categoryResponseDTOs = new ArrayList<>();
        for (Category category : categories) {
            categoryResponseDTOs.add(categoryMapper.entityToDto(category));
        }
        return categoryResponseDTOs;
    }

    public Optional<CategoryResponseDTO> getCategoryById(Long id) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        return categoryOpt.map(category -> categoryMapper.entityToDto(category));
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + id));
        categoryMapper.updateEntityFromDto(dto, category);
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.entityToDto(updatedCategory);
    }

    public void deleteCategory(Long id) {
        if(categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Category not found with id: " + id);
        }
    }

}
