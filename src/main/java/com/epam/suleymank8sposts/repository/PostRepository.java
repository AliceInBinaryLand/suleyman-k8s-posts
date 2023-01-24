package com.epam.suleymank8sposts.repository;

import com.epam.suleymank8sposts.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
