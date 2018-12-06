package karstenroethig.filetags.webapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import karstenroethig.filetags.webapp.controller.formatter.TagFormatter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter
{
	@Override
	public void addFormatters(FormatterRegistry formatterRegistry)
	{
		formatterRegistry.addFormatter(new TagFormatter());
	}
}
