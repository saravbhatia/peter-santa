package com.tripactions.infra.mysql.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class BaseAuditableEntity {
	@Column(name = "date_created")
	protected Instant dateCreated;

	@Column(name = "date_modified")
	protected Instant dateModified;

	@PrePersist
	protected void prePersist() {
		Instant now = Instant.now();
		this.dateCreated = now;
		this.dateModified = now;
	}

	@PreUpdate
	protected void preUpdate() {
		Instant now = Instant.now();
		if (this.dateCreated == null) {
			this.dateCreated = now;
		}
		this.dateModified = now;
	}

	public Instant getDateCreated() {
		return dateCreated;
	}

	public Instant getDateModified() {
		return dateModified;
	}
}