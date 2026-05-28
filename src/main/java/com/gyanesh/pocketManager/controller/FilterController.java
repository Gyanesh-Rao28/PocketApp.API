package com.gyanesh.pocketManager.controller;


import com.gyanesh.pocketManager.dto.ExpenseDto;
import com.gyanesh.pocketManager.dto.FilterDto;
import com.gyanesh.pocketManager.dto.IncomeDto;
import com.gyanesh.pocketManager.service.ExpenseService;
import com.gyanesh.pocketManager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    final private ExpenseService expenseService;
    final private IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDto filter){

        LocalDate startDate = filter.getStartDate() != null? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null? filter.getEndDate() : LocalDate.now();

        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() != null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        if("income".equalsIgnoreCase(filter.getType())){
            List<IncomeDto> incomes = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomes);
        }else if("expense".equalsIgnoreCase(filter.getType())){
            List<ExpenseDto> expense = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return  ResponseEntity.ok(expense);
        }else {
            return ResponseEntity.badRequest().body("Invalid filter type. Must be 'income' or 'expense'.");
        }
    }

}
