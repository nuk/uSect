package org.unbiquitous.games.uSect;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.UUID;

import org.fest.assertions.data.MapEntry;
import org.junit.Test;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uos.core.messageEngine.messages.Response;

public class USectDriverTest {
	@Test public void respondToPlayerCalls(){
		String id = UUID.randomUUID().toString();
		GameSettings settings = new GameSettings();
		settings.put("usect.player.id",id);
		USectDriver driver = new USectDriver(settings);
		Response response = new Response();
		driver.playerInfo(null,response, null);
		assertThat(response.getResponseData())
			.hasSize(1)
			.contains(MapEntry.entry("player.id", id));
	}
}
