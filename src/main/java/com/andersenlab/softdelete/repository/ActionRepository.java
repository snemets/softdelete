package com.andersenlab.softdelete.repository;

import com.andersenlab.softdelete.entity.Action;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ActionRepository extends JpaRepository<Action,Long> {
    @Query("select a from Action a join fetch a.person p where a.id = :id")
    Optional<Action> findByIdEager(Long id);
}
