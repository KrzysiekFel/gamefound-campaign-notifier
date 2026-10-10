package com.github.krzysiekfel.gamefound_campaign_notifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class GamefoundCampaignNotifierApplication {

	public static void main(String[] args) {
		SpringApplication.run(GamefoundCampaignNotifierApplication.class, args);
	}

}
