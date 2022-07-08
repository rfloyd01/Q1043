package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.Artist;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistDAO extends PagingAndSortingRepository<Artist, Integer> {
    Optional<Artist> findArtistByName(String name);
}
