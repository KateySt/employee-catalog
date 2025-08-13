package com.epam.rd.autotasks.springemployeecatalog.entity;

import javax.persistence.*;

@Entity
@Table(name = "DEPARTMENT")
public class DepartmentEntity {
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "LOCATION")
    private String location;

    public DepartmentEntity() {}

    public DepartmentEntity(Long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}