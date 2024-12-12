package com.tripactions;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;

import com.tripactions.notification.log.Markers;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
class MarkersTest {
	@Test
	void markers() {
		try {
			Markers.builder("with properties").put("foo", "bar").build();
			Markers.builder("without properties").build();
			Markers.builder("with properties").put("", "").build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail();
		}
	}
}
