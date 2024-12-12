package com.tripactions.infra.mysql.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person extends BaseAuditableEntity {
	public enum Gender {
		MALE, FEMALE
	}

	private String id;
	private String name;
	private Gender gender;
}
