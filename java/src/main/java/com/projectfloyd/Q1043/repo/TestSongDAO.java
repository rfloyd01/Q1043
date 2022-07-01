package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.TestSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestSongDAO extends JpaRepository<TestSong, Integer> {
    Optional<List<TestSong>> findSongsByName(String name);
    Optional<TestSong> findSongByName(String name);
}
