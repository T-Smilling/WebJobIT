package com.javaweb.jobIT.repository;

import com.javaweb.jobIT.entity.JobPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface JobPostRepository extends JpaRepository<JobPostEntity,Long>, JpaSpecificationExecutor<JobPostEntity> {

}
