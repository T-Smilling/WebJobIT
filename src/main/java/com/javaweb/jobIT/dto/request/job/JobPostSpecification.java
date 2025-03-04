package com.javaweb.jobIT.dto.request.job;

import com.javaweb.jobIT.entity.JobPostEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class JobPostSpecification implements Specification<JobPostEntity> {
    private final JobSearchCriteria criteria;

    public JobPostSpecification(JobSearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<JobPostEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getTitle() != null) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.getTitle().toLowerCase() + "%"));
        }
        if (criteria.getLocation() != null) {
            predicates.add( cb.like(cb.lower(root.get("location")), "%" + criteria.getLocation().toLowerCase() + "%"));
        }
        if (criteria.getJobType() != null) {
            predicates.add(cb.equal(root.get("jobType"), criteria.getJobType()));
        }
        if (criteria.getJobLevel() != null) {
            predicates.add(cb.equal(root.get("jobLevel"), criteria.getJobLevel()));
        }
        if (criteria.getMinSalary() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("salary"), criteria.getMinSalary()));
        }
        if (criteria.getMaxSalary() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("salary"), criteria.getMaxSalary()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}