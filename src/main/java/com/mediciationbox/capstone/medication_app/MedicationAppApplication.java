package com.mediciationbox.capstone.medication_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedicationAppApplication {

	public static void main(String[] args) {
		System.out.println("Java Version: " + System.getProperty("java.version"));
		System.out.println("Java Home: " + System.getProperty("java.home"));
		SpringApplication.run(MedicationAppApplication.class, args);

	}

}

