package com.example.entity.projection;

import java.math.BigDecimal;

public class JobPositionProjection {

	public interface Summary {
		String getName();
		BigDecimal getSalary();
		DepartmentProjection.Summary getDepartment();
	}

}
