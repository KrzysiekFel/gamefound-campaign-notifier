package com.github.krzysiekfel.gamefound_campaign_notifier;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "security.jwt.secret=test-only-jwt-secret-at-least-32-bytes-long")
class GamefoundCampaignNotifierApplicationTests {

	@Test
	void contextLoads() {
	}

}
