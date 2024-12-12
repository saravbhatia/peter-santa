package com.tripactions.notification.log;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.google.common.base.Stopwatch;

@Slf4j
@Aspect
@Component
public class PrintTimeAspect {


	@Around("@annotation(printTime)")
	public Object printTime(ProceedingJoinPoint joinPoint, PrintTime printTime) throws Throwable {
		MDC.put(MdcConstants.START_TIME, Instant.now().toString());
		var sw = Stopwatch.createStarted();
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		Markers.MarkerBuilder marker = Markers.builder(Markers.NOTIFICATIONS_MICROSERVICE)
				.put(MdcConstants.CLASS, className)
				.put(MdcConstants.METHOD, methodName);
		boolean success = false;
		try {
			Object obj = joinPoint.proceed();
			marker.put(MdcConstants.TIME_ELAPSED_MS, sw.stop().elapsed(TimeUnit.MILLISECONDS));
			success = true;
			return obj;
		} finally {
			log.info(marker.build(), "Method {}.{} completed after {}. {}", className, methodName, sw, success ? "successfully" : " with an error");
		}
	}
}
