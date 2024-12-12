package com.tripactions.infra.redis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Person")
public class Person implements Serializable {
	public enum Gender {
		MALE, FEMALE
	}

	private String id;
	private String name;
	private Gender gender;
}
