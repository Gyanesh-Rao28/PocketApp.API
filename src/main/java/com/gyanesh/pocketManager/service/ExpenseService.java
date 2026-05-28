package com.gyanesh.pocketManager.service;

import com.gyanesh.pocketManager.dto.ExpenseDto;
import com.gyanesh.pocketManager.entity.CategoryEntity;
import com.gyanesh.pocketManager.entity.ExpenseEntity;
import com.gyanesh.pocketManager.entity.ProfileEntity;
import com.gyanesh.pocketManager.repository.CategoryRepository;
import com.gyanesh.pocketManager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepo;


    public ExpenseDto addExpense(ExpenseDto expenseDto){

        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepo.findById(expenseDto.getCategoryId()).orElseThrow(()->new RuntimeException("Category Not Found"));

        ExpenseEntity expense = toEntity(expenseDto, category, profile);
        expense = expenseRepo.save(expense);

        return toDto(expense);
    }

    // retrieve all the expenses for current month based on start and end date
    public List<ExpenseDto> getCurrentMonthExpenses(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<ExpenseEntity> expenses = expenseRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);

        return expenses.stream().map(this::toDto).toList();
    }

    // Delete expense by id for current User
    public void deleteExpenseById(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity expense = expenseRepo.findById(expenseId).orElseThrow(
                ()->new RuntimeException("Expense not found")
        );

        if(!profile.getId().equals(expense.getProfile().getId())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        expenseRepo.delete(expense);

    }

    // get lastest 5 expenses for user
    public List<ExpenseDto> getLatestExpenses(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenses = expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return expenses.stream().map(this::toDto).toList();
    }

    // get total sum of expense
    public BigDecimal getTotalExpenseAmount(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total =  expenseRepo.findTotalExpenseByProfileId(profile.getId());
        return total!=null ? total :BigDecimal.ZERO;
    }

    // filter expenses
    public List<ExpenseDto> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){

        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenses = expenseRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate, endDate, keyword, sort);
        return expenses.stream().map(this::toDto).toList();
    }

    // Notification
    public List<ExpenseDto> getExpensesForUserOnDate(Long ProfileId, LocalDate date){
        List<ExpenseEntity> expenses = expenseRepo.findByProfile_IdAndDate(ProfileId,date);
        return expenses.stream().map(this::toDto).toList();
    }

    // Helper Method

    public ExpenseEntity toEntity(ExpenseDto expenseDto, CategoryEntity category, ProfileEntity profile){
        return ExpenseEntity.builder()
                .name(expenseDto.getName())
                .icon(expenseDto.getIcon())
                .amount(expenseDto.getAmount())
                .date(expenseDto.getDate())
                .category(category)
                .profile(profile)
                .build();
    }

    public ExpenseDto toDto(ExpenseEntity expenseEntity){
        return ExpenseDto.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .icon(expenseEntity.getIcon())
                .categoryId(expenseEntity.getCategory() != null? expenseEntity.getCategory().getId(): null)
                .categoryName(expenseEntity.getCategory() != null? expenseEntity.getCategory().getName(): "N/A")
                .amount(expenseEntity.getAmount())
                .date(expenseEntity.getDate())
                .createdAt(expenseEntity.getCreatedAt())
                .updatedAt(expenseEntity.getUpdatedAt())
                .build();
    }


}
