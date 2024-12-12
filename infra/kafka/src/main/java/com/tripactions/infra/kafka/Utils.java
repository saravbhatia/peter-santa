package com.tripactions.infra.kafka;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {
	public static int generateNumber(String uuId) throws StringIndexOutOfBoundsException {
		int number;
		try {
			number = uuId.length()/uuId.substring(3).length();
		}
		catch(StringIndexOutOfBoundsException e) {
			throw e;
		}

		return number;
	}
}
