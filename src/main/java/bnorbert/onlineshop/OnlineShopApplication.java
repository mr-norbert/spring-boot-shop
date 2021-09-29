package bnorbert.onlineshop;

import bnorbert.onlineshop.service.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OnlineShopApplication {

	//@Resource
	//FileStorageService storageService;

	public static void main(String[] args) {
		SpringApplication.run(OnlineShopApplication.class, args);

	}

/*
	@Bean
	CommandLineRunner init(FileStorageService storageService) {
		return (args) -> {
			//storageService.deleteAll();
			//storageService.init();
		};
	}
 */

}
