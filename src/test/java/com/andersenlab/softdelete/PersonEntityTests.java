package com.andersenlab.softdelete;

import com.andersenlab.softdelete.repository.ActionRepository;
import com.andersenlab.softdelete.repository.PersonRepository;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true")
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
        assertTrue(actionRepository.findById(1L).get().getPerson().isDeleted());        //... or lazy fetch (only in transaction, see below)

        assertEquals(0L, personRepository.findAllDeleted2().size());            //The ugliest query condition : where ( person0_.is_deleted = false) and person0_.is_deleted=true
    }

    @Test
    void testLazyLoad() {
        personRepository.deleteById(1L);

        // one more thing.Outside the transaction and with spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true property
        // on lazy fetch you will get the exception because the @Where condition is applied
        assertTrue(actionRepository.findById(1L).isPresent());
        final var action =  actionRepository.findById(1L).get();
        assertThrows(LazyInitializationException.class, () -> System.out.println(action.getPerson()));
    }

}
