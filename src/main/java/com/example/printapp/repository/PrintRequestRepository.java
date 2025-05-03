package com.example.printapp.repository;

import com.example.printapp.model.PrintRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrintRequestRepository extends JpaRepository<PrintRequest, Long> {
    // You can define custom query methods if needed, but basic CRUD operations are handled by JpaRepository
}
