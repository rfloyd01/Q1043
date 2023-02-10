package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.RedbookCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedbookCoinDAO extends JpaRepository<RedbookCoin, Integer> {
    Optional<List<RedbookCoin>> findByCoinName(String coin_name);
    Optional<List<RedbookCoin>> findByCoinType(String coin_type);
}
