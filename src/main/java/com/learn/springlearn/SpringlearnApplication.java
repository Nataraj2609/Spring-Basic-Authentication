package com.learn.springlearn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class
SpringlearnApplication {

	public static List<String> strList = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(SpringlearnApplication.class, args);
	}

}
