package com.dadobt.homework.repository;

import com.dadobt.homework.entity.ApplicationUser;
import com.dadobt.homework.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findOneById(Long id);

    List<Message> findAllByUser(ApplicationUser username);
}
