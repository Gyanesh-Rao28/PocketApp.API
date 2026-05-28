package com.gyanesh.pocketManager.service;

import com.gyanesh.pocketManager.dto.ExpenseDto;
import com.gyanesh.pocketManager.dto.IncomeDto;
import com.gyanesh.pocketManager.dto.RecentTransactionDto;
import com.gyanesh.pocketManager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData(){
        ProfileEntity profile = profileService.getCurrentProfile();

        Map<String, Object> history = new LinkedHashMap<>();

        List<IncomeDto> latestIncome = incomeService.getLatestIncome();
        List<ExpenseDto> latestExpense = expenseService.getLatestExpenses();

        List<RecentTransactionDto> recentTransactionList = Stream.concat(

                latestIncome.stream()
                        .map(income ->
                                RecentTransactionDto.builder()
                                        .id(income.getId())
                                        .profileId(profile.getId())
                                        .icon(income.getIcon())
                                        .name(income.getName())
                                        .amount(income.getAmount())
                                        .date(income.getDate())
                                        .createdAt(income.getCreatedAt())
                                        .updatedAt(income.getUpdatedAt())
                                        .type("income")
                                        .build()
                        ),

                latestExpense.stream()
                        .map(expense ->
                                RecentTransactionDto.builder()
                                        .id(expense.getId())
                                        .profileId(profile.getId())
                                        .icon(expense.getIcon())
                                        .name(expense.getName())
                                        .amount(expense.getAmount())
                                        .date(expense.getDate())
                                        .createdAt(expense.getCreatedAt())
                                        .updatedAt(expense.getUpdatedAt())
                                        .type("expense")
                                        .build()
                        )

        ).sorted((a,b)->{
            int cmp = b.getDate().compareTo(a.getDate());
            if(cmp==0 && a.getCreatedAt()!=null && b.getCreatedAt()!=null){
                return b.getCreatedAt().compareTo(a.getCreatedAt());
            }
            return cmp;
        }).toList();

        history.put("totalBalance", incomeService.getTotalIncomeAmount().subtract(expenseService.getTotalExpenseAmount()));
        history.put("totalIncome", incomeService.getTotalIncomeAmount());
        history.put("totalExpense", expenseService.getTotalExpenseAmount());
        history.put("recentExpenses", latestExpense);
        history.put("recentIncome", latestIncome);
        history.put("recentTransactions", recentTransactionList);

        return history;
    }
}
