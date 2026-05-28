package com.gyanesh.pocketManager.service;

import com.gyanesh.pocketManager.dto.IncomeDto;
import com.gyanesh.pocketManager.entity.CategoryEntity;
import com.gyanesh.pocketManager.entity.IncomeEntity;
import com.gyanesh.pocketManager.entity.ProfileEntity;
import com.gyanesh.pocketManager.repository.CategoryRepository;
import com.gyanesh.pocketManager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomeRepo;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepo;

    public IncomeDto addIncome(IncomeDto incomeDto){

        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepo.findById(incomeDto.getCategoryId()).orElseThrow(()->new RuntimeException("Category Not Found"));

        IncomeEntity income = toEntity(incomeDto, category, profile);
        income = incomeRepo.save(income);

        return toDto(income);
    }

    // retrieve all the income for current month based on start and end date
    public List<IncomeDto> getCurrentMonthIncomes(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<IncomeEntity> expenses = incomeRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);

        return expenses.stream().map(this::toDto).toList();
    }


    // Delete expense by id for current User
    public void deleteIncomeById(Long incomeId){
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity expense = incomeRepo.findById(incomeId).orElseThrow(
                ()->new RuntimeException("Expense not found")
        );

        if(!profile.getId().equals(expense.getProfile().getId())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        incomeRepo.delete(expense);

    }

    // get lastest 5 expenses for user
    public List<IncomeDto> getLatestIncome(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> income = incomeRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return income.stream().map(this::toDto).toList();
    }

    // get total sum of expense
    public BigDecimal getTotalIncomeAmount(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepo.findTotalIncomeByProfileId(profile.getId());
        return total!=null ? total :BigDecimal.ZERO;
    }


    // filter income
    public List<IncomeDto> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){

        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> expenses = incomeRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return expenses.stream().map(this::toDto).toList();
    }



    // Helper Method

    public IncomeEntity toEntity(IncomeDto incomeDto, CategoryEntity category, ProfileEntity profile){
        return IncomeEntity.builder()
                .name(incomeDto.getName())
                .icon(incomeDto.getIcon())
                .amount(incomeDto.getAmount())
                .date(incomeDto.getDate())
                .category(category)
                .profile(profile)
                .build();
    }

    public IncomeDto toDto(IncomeEntity incomeEntity){
        return IncomeDto.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .categoryId(incomeEntity.getCategory() != null? incomeEntity.getCategory().getId(): null)
                .categoryName(incomeEntity.getCategory() != null? incomeEntity.getCategory().getName(): "N/A")
                .amount(incomeEntity.getAmount())
                .date(incomeEntity.getDate())
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .build();
    }
}
