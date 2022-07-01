package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongDAO extends JpaRepository<Song, Integer> {
    Optional<List<Song>> findSongByTitle(String title);
}
