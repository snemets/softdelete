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

        personRepository.deleteById(1L);                                                //Now deleteById do what we declared in @SqlDelete. [I'm the master of Hibernate!!!]

        assertEquals(0L, personRepository.count());                             // regular queries can not see deleted entity row
        assertEquals(0L, personRepository.findAll().size());                    // regular queries can not see deleted entity row

        assertEquals(1L, personRepository.findAllDeleted().size());             // native queries are the way to skip @Where condition :)

        assertEquals(1L, actionRepository.count());

        assertTrue(actionRepository.findByIdEager(1L).isPresent());
        assertTrue(actionRepository.findByIdEager(1L).get().getPerson().isDeleted());   //However, dependent entities could fetch the entity marked as deleted with eager...

        assertTrue(actionRepository.findById(1L).isPresent());
        assertTrue(actionRepository.findById(1L).get().getPerson().isDeleted());        //... or lazy fetch

        assertEquals(0L, personRepository.findAllDeleted2().size());            //The ugliest query condition : where ( person0_.is_deleted = false) and person0_.is_deleted=true
    }

}
