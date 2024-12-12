package com.tripactions.taCallBot.config;

import jakarta.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tripactions.common.web.filter.MdcGeneralFilter;

@Configuration
public class FilterConfiguration {

	@Bean
	public Filter mdcGeneralFilter() {
		return new MdcGeneralFilter();
	}
}
