package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "job_position")
public class JobPosition {

	@Id
	@Column(name = "id", nullable = false)
	private Long id;

	@Size(max = 250)
	@Column(name = "name", length = 250)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	@Column(name = "salary")
	private BigDecimal salary;

	@Column(name = "vacation_days")
	private Integer vacationDays;

}