package com.andersenlab.softdelete;

import com.andersenlab.softdelete.repository.ActionRepository;
import com.andersenlab.softdelete.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Sql(statements = {"TRUNCATE TABLE person CASCADE", "insert into person values (1, 'John', false)", "insert into action values (1, 1, 'Greeting')" })
class PersonEntityTests {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Test
    @Transactional
    void deleteAndGetAll() {
        assertEquals(1L, personRepository.count());

        personRepository.deleteById(1L);

        assertEquals(0L, personRepository.count());
        assertEquals(0L, personRepository.findAll().size());

        assertEquals(1L, personRepository.findAllDeleted().size());

        assertEquals(1L, actionRepository.count());
        assertTrue(actionRepository.findByIdEager(1L).isPresent());
        assertTrue(actionRepository.findByIdEager(1L).get().getPerson().isDeleted());

        assertTrue(actionRepository.findById(1L).isPresent());
        assertTrue(actionRepository.findById(1L).get().getPerson().isDeleted());


        assertEquals(0L, personRepository.findAllDeleted2().size());
    }

}
