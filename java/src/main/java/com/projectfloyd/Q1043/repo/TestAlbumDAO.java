package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.TestAlbum;
import com.projectfloyd.Q1043.models.TestSong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestAlbumDAO extends JpaRepository<TestAlbum, Integer> {
    Optional<TestAlbum> findAlbumByTitle(String title);
}
