package com.tripactions;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.tripactions.notification.log.PrintTime;
import com.tripactions.notification.log.PrintTimeAspect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@Slf4j
class PrintTimeAspectTest {

	@Test
	void printTime() {
		PrintTimeAspect printTimeAspect = new PrintTimeAspect();
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		PrintTime printTime = Mockito.mock(PrintTime.class);
		try {
			Signature signature = Mockito.mock(Signature.class);
			when(joinPoint.getTarget()).thenReturn(this);
			when(joinPoint.getSignature()).thenReturn(signature);
			when(signature.getName()).thenReturn("myMethod");
			when(joinPoint.proceed()).thenReturn(Collections.emptyMap());

			printTimeAspect.printTime(joinPoint, printTime);

			when(joinPoint.proceed()).thenReturn(Collections.emptyList());
			printTimeAspect.printTime(joinPoint, printTime);

			when(joinPoint.proceed()).thenReturn("hello world");
			Object result = printTimeAspect.printTime(joinPoint, printTime);
			assertEquals("hello world", result);
		} catch (Throwable throwable) {
			log.error(throwable.getMessage(), throwable);
			fail();
		}

		try {
			when(joinPoint.proceed()).thenThrow(new RuntimeException("just a test"));
			printTimeAspect.printTime(joinPoint, printTime);
		} catch (RuntimeException throwable) {
			log.info("pass negative test");
		} catch (Throwable throwable) {
			log.error(throwable.getMessage(), throwable);
			fail();
		}
	}
}
