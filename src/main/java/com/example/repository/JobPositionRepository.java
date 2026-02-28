package com.example.repository;

import com.example.entity.projection.JobPositionProjection;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.entity.JobPosition;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {

//	@EntityGraph(attributePaths = "department.subDepartments")
//	@Query(value = "SELECT jp FROM JobPosition jp")
	@Query(value = "SELECT jp FROM JobPosition jp JOIN FETCH jp.department LEFT JOIN FETCH jp.department.subDepartments")
	Stream<JobPosition> findAllStreamed();


	@Query(value = "SELECT jp.name name, jp.salary salary, jp.department department FROM JobPosition jp LEFT JOIN jp.department")
	<T> List<T> findProjection(Class<T> projectionType);


	List<JobPositionProjection.Summary> findByName(String name);

}
