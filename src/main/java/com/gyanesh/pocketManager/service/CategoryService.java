package com.gyanesh.pocketManager.service;

import com.gyanesh.pocketManager.dto.CategoryDto;
import com.gyanesh.pocketManager.entity.CategoryEntity;
import com.gyanesh.pocketManager.entity.ProfileEntity;
import com.gyanesh.pocketManager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepo;


    public CategoryDto saveCategory(CategoryDto categoryDto){

        ProfileEntity profile = profileService.getCurrentProfile();

        if(profile==null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UnAuthenticated User");
        }

        if(categoryRepo.existsByNameAndProfileId(categoryDto.getName(), profile.getId() )){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category With this name already exists.");
        }

        CategoryEntity newCategory = toEntity(categoryDto, profile);
        return toDto(categoryRepo.save(newCategory));
    }


    // get category for current user
    public List<CategoryDto> getCategoriesForCurrentUser(){

        ProfileEntity currentUser = profileService.getCurrentProfile();

        if(currentUser==null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UnAuthenticated User");
        }

        List<CategoryEntity> categories = categoryRepo.findByProfileId(currentUser.getId());

        return categories.stream().map(this::toDto).toList();

    }

    // get category by type for current user
    public List<CategoryDto> getCategoriesForCurrentUserByType(String type){

        ProfileEntity currentUser = profileService.getCurrentProfile();

        if(currentUser==null){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "UnAuthenticated User");
        }

        List<CategoryEntity> categoriesByType = categoryRepo.findByTypeAndProfileId(type ,currentUser.getId());

        return categoriesByType.stream().map(this::toDto).toList();

    }

    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto){

        ProfileEntity currentUser = profileService.getCurrentProfile();

        CategoryEntity currCategory = categoryRepo.findByIdAndProfileId(categoryId, currentUser.getId())
                .orElseThrow(()->new RuntimeException("Category Not Found"));

        currCategory.setName(categoryDto.getName());
        currCategory.setIcon(categoryDto.getIcon());

        CategoryEntity updatedCategory = categoryRepo.save(currCategory);

        return toDto(updatedCategory);

    }




    // helper method

    private CategoryEntity toEntity(CategoryDto categoryDto, ProfileEntity profile){

        return CategoryEntity.builder()
                .name(categoryDto.getName())
                .type(categoryDto.getType())
                .icon(categoryDto.getIcon())
                .profile(profile)
                .build();

    }

    private CategoryDto toDto(CategoryEntity categoryEntity){

        return CategoryDto.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile()!=null ? categoryEntity.getProfile().getId() : null )
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }

}
