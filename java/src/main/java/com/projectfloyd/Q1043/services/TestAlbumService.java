package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.TestAlbum;
import com.projectfloyd.Q1043.models.TestSong;
import com.projectfloyd.Q1043.repo.TestAlbumDAO;
import com.projectfloyd.Q1043.repo.TestSongDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class TestAlbumService {

    private TestAlbumDAO testAlbumDAO;
    private TestSongDAO testSongDAO;

    @Autowired
    public TestAlbumService(TestAlbumDAO testAlbumDAO, TestSongDAO testSongDAO) {

        this.testAlbumDAO = testAlbumDAO;
        this.testSongDAO = testSongDAO;
    }

    public TestAlbum getAlbumById(int id) { return testAlbumDAO.findById(id).orElse(null); }

    public TestAlbum getAlbumByTitle(String title) { return testAlbumDAO.findAlbumByTitle(title).orElse(null); }

    public TestAlbum saveAlbum(TestAlbum album) {
        try {
            return testAlbumDAO.save(album);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }


    public TestAlbum addAlbum(int id, TestSong song) {
        //first check to see if the test song is in the database
        Optional<TestSong> testSong = testSongDAO.findById(song.getId());

        if (testSong.isPresent()) {
            Optional<TestAlbum> testAlbum = testAlbumDAO.findById(id);

            if (testAlbum.isPresent()) {
                //set the album variable of the song
                TestSong actualSong = testSong.get();
                actualSong.setAlbum(testAlbum.get());

                //save in the db
                testSongDAO.save(actualSong);

                return testAlbum.get();
            }
        }

        return null;
    }
}
