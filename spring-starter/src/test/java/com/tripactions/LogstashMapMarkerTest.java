package com.tripactions;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.tripactions.notification.log.LogstashMapMarker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class LogstashMapMarkerTest {
	@Test
	void markerTest() {
		Map<String, Object> entries = new HashMap<>();
		entries.put("foo", "bar");
		LogstashMapMarker marker1 = new LogstashMapMarker(entries);
		LogstashMapMarker marker2 = new LogstashMapMarker(entries);
		LogstashMapMarker marker3 = new LogstashMapMarker(entries);
		log.info("{} {}", marker1, marker2);
		assertFalse(marker1.hasChildren());
		assertTrue(marker1.contains(marker2));
		marker1.add(marker2);
		assertFalse(marker1.hasChildren());
		assertTrue(marker1.contains(marker2));
		assertTrue(marker1.contains(marker3));
		marker1.add(marker2);
		marker2.add(marker1);
		marker1.add(marker3);

		assertFalse(marker1.iterator().hasNext());

		Set<Object> set = new HashSet<>();
		set.add(marker1);
		set.add(marker2);
		assertThat(set.size()).isEqualTo(1);
		log.info("{} {}", marker1, marker2);

		marker1.remove(marker2);
	}
}
