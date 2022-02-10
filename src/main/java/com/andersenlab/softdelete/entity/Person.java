package com.andersenlab.softdelete.entity;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "person")
@Getter
@Setter
@SQLDelete(sql = "update person set is_deleted = true where id = ?")
@Where(clause = "is_deleted = false")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "personSequence")
    @SequenceGenerator(name = "personSequence", sequenceName = "person_sequence", allocationSize = 1)
    private Long id;

    private String name;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @OneToMany(targetEntity = Action.class, mappedBy = "person")
    private List<Action> action;
}
