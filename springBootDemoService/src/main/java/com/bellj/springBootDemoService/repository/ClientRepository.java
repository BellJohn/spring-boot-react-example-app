package com.bellj.springBootDemoService.repository;

import com.bellj.springBootDemoService.model.Client;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
