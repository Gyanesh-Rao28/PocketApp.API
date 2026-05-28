package com.gyanesh.pocketManager.repository;

import com.gyanesh.pocketManager.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {


    // Select * from tbl_categories where profile_id = ?1
    List<CategoryEntity> findByProfileId(Long profileId);

    // select * from tbl_category where profile_id =?1 and id=?2;
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    // select * from tbl_category where type=?1 and profile_id =?2;
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    // select * from tbl_category where name=?1 and profileId=?2;
    Boolean existsByNameAndProfileId(String name, Long profileId);
}
