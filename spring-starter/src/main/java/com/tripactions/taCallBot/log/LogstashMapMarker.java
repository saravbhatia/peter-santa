package com.tripactions.notification.log;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import net.logstash.logback.marker.MapEntriesAppendingMarker;

public class LogstashMapMarker extends MapEntriesAppendingMarker {
	@Getter
	private final Map<String, Object> map = new HashMap<>();

	public LogstashMapMarker(Map<String, Object> in) {
		super(in);
		map.putAll(in);
	}
}
