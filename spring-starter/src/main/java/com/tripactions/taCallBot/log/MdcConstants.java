package com.tripactions.notification.log;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MdcConstants {
	public static final String TIME_ELAPSED_MS = "timeElapsedMs";
	public static final String CLASS = "class";
	public static final String METHOD = "method";
	public static final String START_TIME = "startTime";
	public static final String NOTIFICATION_UUID = "notificationUuid";
	public static final String NOTIFICATION_TYPE = "notification_type";
	public static final String NOTIFICATION_CHANNEL = "notification_channel";
	public static final String TEMPLATE_NAME = "template_name";
	public static final String USER_UUID = "user_uuid";
	public static final String BOOKING_ID = "booking_id";
	public static final String USER_EMAIL = "user_email";
}
