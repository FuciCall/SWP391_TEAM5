package com.gha.gender_healthcare_api.repository;

import com.gha.gender_healthcare_api.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
