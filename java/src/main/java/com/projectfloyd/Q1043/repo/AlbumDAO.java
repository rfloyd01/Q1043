package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumDAO extends JpaRepository<Album, Integer> {
    Optional<Album> findAlbumByTitle(String title);
}
