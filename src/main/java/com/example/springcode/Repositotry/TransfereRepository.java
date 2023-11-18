package com.example.springcode.Repositotry;

import com.example.springcode.Entities.Profile;
import com.example.springcode.Entities.Transfere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransfereRepository extends JpaRepository<Transfere,Long> {
    List<Transfere> findByProfilFromIdOrProfileTo(Long id,Long id2);
    List<Transfere> findByProfilFromId(Long id);
}
