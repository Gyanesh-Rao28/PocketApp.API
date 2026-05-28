package com.gyanesh.pocketManager.repository;

import com.gyanesh.pocketManager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long> {


    // Select * from tbl_income where profile_id = ?1 order by date desc;
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    // Select * from tbl_income where profile_id = ?1 order by date desc limit 5;
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(long profileId);

    @Query("select sum(e.amount) from IncomeEntity e where e.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);

    // select * from tbl_income where profile_id = ?1 and date between ?2 and ?3 and name like %?4%
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort
    );

    // select * from tbl_income profile
    List<IncomeEntity> findByProfileIdAndDateBetween(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate
    );
}
