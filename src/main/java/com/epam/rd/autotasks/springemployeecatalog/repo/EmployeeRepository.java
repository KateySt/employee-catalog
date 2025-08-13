package com.epam.rd.autotasks.springemployeecatalog.repo;

import com.epam.rd.autotasks.springemployeecatalog.entity.EmployeeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Long> {
    Page<EmployeeEntity> findByManagerId(Long managerId, Pageable pageable);

    Page<EmployeeEntity> findByDepartmentId(Long departmentId, Pageable pageable);

    @Query("SELECT e FROM EmployeeEntity e JOIN DepartmentEntity d ON e.departmentId = d.id WHERE d.name = :departmentName")
    Page<EmployeeEntity> findByDepartmentName(@Param("departmentName") String departmentName, Pageable pageable);
}