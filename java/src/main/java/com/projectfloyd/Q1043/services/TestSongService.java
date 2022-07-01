package com.projectfloyd.Q1043.services;

import com.projectfloyd.Q1043.models.TestSong;
import com.projectfloyd.Q1043.repo.TestSongDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestSongService {

    private TestSongDAO testSongDAO;
    private int callNumber = 0;

    @Autowired
    public TestSongService(TestSongDAO testSongDAO) {
        this.testSongDAO = testSongDAO;
    }

    public TestSong getSongById(int id) { return testSongDAO.findById(id).orElse(null); }

    public List<TestSong> getSongsByName(String name) { return testSongDAO.findSongsByName(name).orElse(null); }

    public TestSong saveSong(TestSong song) {
        try {
            return testSongDAO.save(song);
        } catch (Exception e) {
            return null;
        }
    }


}
