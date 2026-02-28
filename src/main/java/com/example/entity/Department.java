package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "department")
public class Department {

	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 50)
	@Column(name = "name", length = 50)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Department parent;

	@OneToMany(mappedBy = "parent")
	private Set<Department> subDepartments = new LinkedHashSet<>();

	@OneToMany(mappedBy = "department")
	private Set<JobPosition> jobPositions = new LinkedHashSet<>();

}