package com.example.entity.projection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.PersistenceCreator;

public class UserProjection {

	public interface UserSummary {

		String getName();
		String getUsername();

		default String getInfo() {
			return getName() + " " + getUsername();
		}

//		@Value("#{target.name} #{target.username}")
//		String getInfo();
	}


	public record UserShort(String username, String email) {

		public String getInfo() {
			return username() + " " + email();
		}

	}

}
