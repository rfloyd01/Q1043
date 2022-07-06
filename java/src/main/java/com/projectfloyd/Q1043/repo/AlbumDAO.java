package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.Album;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumDAO extends PagingAndSortingRepository<Album, Integer> {
    Optional<Album> findAlbumByTitle(String title);
}
