package de.ml.boot;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerProvider {

	@Produces
	private Logger getLogger(InjectionPoint ip){
		return LoggerFactory.getLogger(ip.getMember().getDeclaringClass().getName());
	}

}
