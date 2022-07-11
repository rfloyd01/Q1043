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

    //I don't like having a separate function for each of these columns. There must
    //be some good way to combine the Greater Than logic with a Sort object
    Page<Song> findByRank2001GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2002GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2003GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2004GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2005GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2006GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2007GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2008GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2009GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2010GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2011GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2012GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2013GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2014GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2015GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2016GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2017GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2018GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2019GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2020GreaterThan(Integer minimum, Pageable pageable);
    Page<Song> findByRank2021GreaterThan(Integer minimum, Pageable pageable);

}
