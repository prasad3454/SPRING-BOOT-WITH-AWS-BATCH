package com.awsbatch.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import com.awsbatch.model.Product;

public interface ProductRepository extends JpaRepository<Product, Serializable>{

}
