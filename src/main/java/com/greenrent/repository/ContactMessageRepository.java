package com.greenrent.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.greenrent.domain.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage ,  Long>{

}
