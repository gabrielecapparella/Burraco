package it.gabrielecapparella.burraco.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/tables").setViewName("tables");
		registry.addViewController("/").setViewName("index");
		//registry.addViewController("/login").setViewName("login");
	}

//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/js/**")
//				.addResourceLocations("classpath:/resources/static/js/");
//	}

//	@Bean
//	public ViewResolver viewResolver() {
//		InternalResourceViewResolver bean = new InternalResourceViewResolver();
//		bean.setPrefix("/resources/templates/");
//		bean.setSuffix(".html");
//
//		return bean;
//	}
}
