package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.CleanRawData;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CleanRawDAO extends PagingAndSortingRepository<CleanRawData, Integer> {
}
