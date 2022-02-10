package com.andersenlab.softdelete.repository;

import com.andersenlab.softdelete.entity.Person;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

    @Query(value = "select p.* from person p where p.is_deleted = true", nativeQuery = true)
    List<Person> findAllDeleted();

    @Query(value = "select p from Person p where p.isDeleted = true")
    List<Person> findAllDeleted2();

}