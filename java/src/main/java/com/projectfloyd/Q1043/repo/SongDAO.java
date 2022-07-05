package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongDAO extends PagingAndSortingRepository<Song, Integer> {
    Optional<List<Song>> findSongByTitle(String title);
}
