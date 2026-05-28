package com.gyanesh.pocketManager.repository;

import com.gyanesh.pocketManager.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    // Select * from tbl_expense where profile_id = ?1 order by date desc;
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // Select * from tbl_expense where profile_id = ?1 order by date desc limit 5;
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(long profileId);


    @Query("select sum(e.amount) from ExpenseEntity e where e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    // select * from tbl_expense where profile_id = ?1 and date between ?2 and ?3 and name like %?4%
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // select * from tbl_expense profile
    List<ExpenseEntity> findByProfileIdAndDateBetween(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<ExpenseEntity> findByProfile_IdAndDate(Long profileId, LocalDate date);

}
