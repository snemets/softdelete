package com.andersenlab.softdelete;

import com.andersenlab.softdelete.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Sql(statements = {"TRUNCATE TABLE person", "insert into person values (1, 'John', false)"})
class PersonEntityTests {

    @Autowired
    private PersonRepository personRepository;

    @Test
    void deleteAndGetAll() {
        assertEquals(1L, personRepository.count());

        personRepository.deleteById(1L);

        assertEquals(0L, personRepository.count());
        assertEquals(0L, personRepository.findAll().size());
    }

}
