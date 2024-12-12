package com.tripactions.notification.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Markers {
	public static final String NOTIFICATIONS_MICROSERVICE = "NOTIFICATIONS_MICROSERVICE";
	public static final String NOTIFICATIONS_KAFKA_PRODUCER = "NOTIFICATIONS_KAFKA_PRODUCER";
	public static final String NOTIFICATIONS_KAFKA_CONSUMER = "NOTIFICATIONS_KAFKA_CONSUMER";
	public static final String NOTIFICATIONS_PROCESSOR = "NOTIFICATIONS_PROCESSOR";

	private Markers() {
		// static method only
	}

	public static MarkerBuilder builder(String name) {
		return new MarkerBuilder(name);
	}

	public static class MarkerBuilder {
		private final Map<String, Object> entries = new HashMap<>();
		private final String name;

		private MarkerBuilder(String name) {
			this.name = name;
		}

		public MarkerBuilder put(String key, Object value) {
			Optional.ofNullable(value).ifPresent(val -> entries.put(key, value));
			return this;
		}

		public Marker build() {
			if (!entries.isEmpty()) {
				var marker = MarkerFactory.getDetachedMarker(name);
				marker.add(new LogstashMapMarker(entries));
				return marker;
			} else {
				return MarkerFactory.getMarker(name);
			}
		}
	}
}
