package com.gyanesh.pocketManager.controller;

import com.gyanesh.pocketManager.dto.ExpenseDto;
import com.gyanesh.pocketManager.dto.IncomeDto;
import com.gyanesh.pocketManager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/income")
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDto> addIncome(@RequestBody IncomeDto dto){

        IncomeDto response = incomeService.addIncome(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDto>> getExpenses(){
        return ResponseEntity.ok(incomeService.getCurrentMonthIncomes());
    }

    @DeleteMapping("/{incomeId}")
    public ResponseEntity<String> deleteIncome(@PathVariable Long incomeId){
        incomeService.deleteIncomeById(incomeId);
        return ResponseEntity.ok("Income deleted");
    }

    @GetMapping("/latestIncome")
    public ResponseEntity<List<IncomeDto>> getLatestIncomes(){
        return ResponseEntity.ok(incomeService.getLatestIncome());
    }

    @GetMapping("/totalIncomeAmount")
    public ResponseEntity<BigDecimal> getTotalIncomesAmount(){
        return ResponseEntity.ok(incomeService.getTotalIncomeAmount());
    }
}
