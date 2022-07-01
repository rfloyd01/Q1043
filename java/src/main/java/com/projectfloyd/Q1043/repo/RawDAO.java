package com.projectfloyd.Q1043.repo;

import com.projectfloyd.Q1043.models.RawData;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawDAO extends PagingAndSortingRepository<RawData, Integer> {
}
