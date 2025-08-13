package com.epam.rd.autotasks.springemployeecatalog.service;


import com.epam.rd.autotasks.springemployeecatalog.domain.*;
import com.epam.rd.autotasks.springemployeecatalog.entity.DepartmentEntity;
import com.epam.rd.autotasks.springemployeecatalog.entity.EmployeeEntity;
import com.epam.rd.autotasks.springemployeecatalog.repo.DepartmentRepository;
import com.epam.rd.autotasks.springemployeecatalog.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    public Page<Employee> getAllEmployees(int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);
        Page<EmployeeEntity> entities = employeeRepository.findAll(pageable);
        return entities.map(this::convertToEmployeeWithManager);
    }

    public Optional<Employee> getEmployeeById(Long id, boolean fullChain) {
        Optional<EmployeeEntity> entity = employeeRepository.findById(id);
        if (entity.isPresent()) {
            Employee employee;
            if (fullChain) {
                employee = buildFullManagerChain(convertToEmployeeWithManager(entity.get()));
            } else {
                employee = convertToEmployeeWithManager(entity.get());
            }
            return Optional.of(employee);
        }
        return Optional.empty();
    }

    public Page<Employee> getEmployeesByManager(Long managerId, int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);
        Page<EmployeeEntity> entities = employeeRepository.findByManagerId(managerId, pageable);
        return entities.map(this::convertToEmployeeWithManager);
    }

    public Page<Employee> getEmployeesByDepartment(String departmentIdentifier, int page, int size, String sort) {
        Pageable pageable = createPageable(page, size, sort);
        Page<EmployeeEntity> entities;

        try {
            Long departmentId = Long.parseLong(departmentIdentifier);
            entities = employeeRepository.findByDepartmentId(departmentId, pageable);
        } catch (NumberFormatException e) {
            entities = employeeRepository.findByDepartmentName(departmentIdentifier, pageable);
        }

        return entities.map(this::convertToEmployeeWithManager);
    }

    private Employee convertToEmployeeWithManager(EmployeeEntity entity) {
        FullName fullName = new FullName(entity.getFirstName(), entity.getLastName(), entity.getMiddleName());

        Department department = null;
        if (entity.getDepartmentId() != null) {
            Optional<DepartmentEntity> deptEntity = departmentRepository.findById(entity.getDepartmentId());
            if (deptEntity.isPresent()) {
                DepartmentEntity d = deptEntity.get();
                department = new Department(d.getId(), d.getName(), d.getLocation());
            }
        }

        Employee manager = null;
        if (entity.getManagerId() != null) {
            Optional<EmployeeEntity> managerEntity = employeeRepository.findById(entity.getManagerId());
            if (managerEntity.isPresent()) {
                manager = convertToEmployeeWithoutManager(managerEntity.get());
            }
        }

        return new Employee(
                entity.getId(),
                fullName,
                entity.getPosition(),
                entity.getHireDate(),
                entity.getSalary(),
                manager,
                department
        );
    }

    private Employee convertToEmployeeWithoutManager(EmployeeEntity entity) {
        FullName fullName = new FullName(entity.getFirstName(), entity.getLastName(), entity.getMiddleName());

        Department department = null;
        if (entity.getDepartmentId() != null) {
            Optional<DepartmentEntity> deptEntity = departmentRepository.findById(entity.getDepartmentId());
            if (deptEntity.isPresent()) {
                DepartmentEntity d = deptEntity.get();
                department = new Department(d.getId(), d.getName(), d.getLocation());
            }
        }

        return new Employee(
                entity.getId(),
                fullName,
                entity.getPosition(),
                entity.getHireDate(),
                entity.getSalary(),
                null,
                department
        );
    }


    private Employee buildFullManagerChain(Employee employee) {
        if (employee.getManager() == null) {
            return employee;
        }

        List<Employee> managerChain = new ArrayList<>();
        Employee currentManager = employee.getManager();

        while (currentManager != null) {
            Optional<EmployeeEntity> managerEntity = employeeRepository.findById(currentManager.getId());
            if (managerEntity.isPresent()) {
                currentManager = convertToEmployeeWithManager(managerEntity.get());
                managerChain.add(currentManager);
                currentManager = currentManager.getManager();
            } else {
                break;
            }
        }

        Employee topManager = null;
        for (int i = managerChain.size() - 1; i >= 0; i--) {
            Employee mgr = managerChain.get(i);
            if (i == managerChain.size() - 1) {
                topManager = new Employee(
                        mgr.getId(), mgr.getFullName(), mgr.getPosition(),
                        mgr.getHired(), mgr.getSalary(), null, mgr.getDepartment()
                );
            } else {
                topManager = new Employee(
                        mgr.getId(), mgr.getFullName(), mgr.getPosition(),
                        mgr.getHired(), mgr.getSalary(), topManager, mgr.getDepartment()
                );
            }
        }

        return new Employee(
                employee.getId(),
                employee.getFullName(),
                employee.getPosition(),
                employee.getHired(),
                employee.getSalary(),
                topManager,
                employee.getDepartment()
        );
    }

    private Pageable createPageable(int page, int size, String sort) {
        Sort.Direction direction = Sort.Direction.ASC;
        String property = "id";

        if (sort != null && !sort.isEmpty()) {
            switch (sort.toLowerCase()) {
                case "lastname":
                    property = "lastName";
                    break;
                case "hired":
                    property = "hireDate";
                    break;
                case "position":
                    property = "position";
                    break;
                case "salary":
                    property = "salary";
                    break;
            }
        }

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}