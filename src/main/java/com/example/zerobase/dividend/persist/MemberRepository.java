package com.example.zerobase.dividend.persist;

import com.example.zerobase.dividend.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    Optional<MemberEntity> findByUsername(String userName);

    boolean existsByUsername(String userName);

}
