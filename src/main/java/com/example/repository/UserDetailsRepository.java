package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.entity.UserDetails;

import java.util.stream.Stream;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

	@Query("SELECT ud FROM UserDetails ud")
	Stream<UserDetails> findAllStreamed();

}
