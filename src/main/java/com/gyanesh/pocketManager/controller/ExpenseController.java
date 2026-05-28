package com.gyanesh.pocketManager.controller;


import com.gyanesh.pocketManager.dto.ExpenseDto;
import com.gyanesh.pocketManager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/expense")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDto> addExpense(@RequestBody ExpenseDto dto){

        ExpenseDto response = expenseService.addExpense(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDto>> getExpenses(){
        return ResponseEntity.ok(expenseService.getCurrentMonthExpenses());
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long expenseId){
        expenseService.deleteExpenseById(expenseId);
        return ResponseEntity.ok("Expense deleted");
    }

    @GetMapping("/latestExpense")
    public ResponseEntity<List<ExpenseDto>> getLatestExpenses(){
        return ResponseEntity.status(HttpStatus.OK).body(expenseService.getLatestExpenses());
    }

    @GetMapping("/totalExpenseAmount")
    public ResponseEntity<BigDecimal> getTotalExpenseAmount(){
        return ResponseEntity.ok(expenseService.getTotalExpenseAmount());
    }

}
