package com.gyanesh.pocketManager.repository;

import com.gyanesh.pocketManager.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    // select * from tbl_profile where email=?
    Optional<ProfileEntity> findByEmail(String email);

    // select * from tbl_profile where activationToken=?;
    Optional<ProfileEntity> findByActivationToken(String activationToken);

}
