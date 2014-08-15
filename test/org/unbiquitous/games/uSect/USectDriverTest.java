package org.unbiquitous.games.uSect;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.unbiquitous.games.uSect.TestUtils.setUpEnvironment;

import java.util.UUID;

import org.fest.assertions.data.MapEntry;
import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.objects.Player;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.applicationManager.CallContext;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDevice;
import org.unbiquitous.uos.core.messageEngine.messages.Call;
import org.unbiquitous.uos.core.messageEngine.messages.Response;

public class USectDriverTest {
	
	private GameSettings settings;
	private Environment e;
	private Gateway gateway;
	
	@Before public void setUp(){
		gateway = mock(Gateway.class);
		GameComponents.put(Gateway.class, gateway);
		settings = new GameSettings();
		e = setUpEnvironment(settings);
	}
	
	@Test public void respondToConnectWithPlayerId() {
		UUID id = UUID.randomUUID();
		settings.put("usect.player.id", id.toString());
		e.addPlayer(new Player(id));
		
		USectDriver driver = new USectDriver(settings, e);
		Response response = new Response();
		driver.connect(null, response, mock(CallContext.class));
		
		assertThat(response.getResponseData()).hasSize(1).contains(
				MapEntry.entry("player.id", id.toString()));
	}
	
	@Test public void connectCallingDeviceWithPlayer() throws Exception{
		UUID id = UUID.randomUUID();
		settings.put("usect.player.id", id.toString());
		Player p = e.addPlayer(new Player(id));
		
		USectDriver driver = new USectDriver(settings, e);
		CallContext ctx =  mock(CallContext.class);
		UpDevice target = new UpDevice("Target");
		when(ctx.getCallerDevice()).thenReturn(target);
		
		driver.connect(null, new Response(), ctx);
		p.call();
		
		verify(gateway).callService(eq(target), any(Call.class));
	}
	
	@Test public void redirectCallToCorrespondingPlayer() {
		Player p1 = e.addPlayer(mock(Player.class));
		Player p2 = e.addPlayer(mock(Player.class));
		when(p2.id()).thenReturn(UUID.randomUUID());
		Player p3 = e.addPlayer(mock(Player.class));
		
		USectDriver driver = new USectDriver(settings, e);
		Call call = new Call();
		call.addParameter("id", p2.id().toString());
		driver.call(call, null, null);
		
		verify(p1,times(0)).call();
		verify(p2,times(1)).call();
		verify(p3,times(0)).call();
	}
}
