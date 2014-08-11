package org.unbiquitous.games.uSect.environment;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.unbiquitous.games.uSect.TestUtils.executeThisManyTurns;
import static org.unbiquitous.games.uSect.TestUtils.setUpEnvironment;

import java.util.Arrays;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.stubbing.OngoingStubbing;
import org.unbiquitous.uImpala.engine.core.GameComponents;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.adaptabitilyEngine.ServiceCallException;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDevice;
import org.unbiquitous.uos.core.messageEngine.messages.Call;
import org.unbiquitous.uos.core.messageEngine.messages.Response;

public class Environment_uOSIntegration {
	
	private Environment e;
	private Gateway gateway;
	
	@Before public void setUp(){
		gateway = mock(Gateway.class);
		GameComponents.put(Gateway.class, gateway);
		e = setUpEnvironment();
	}
	
	@After public void tearDown(){
		GameComponents.put(Gateway.class, null);
	}
	
	@Test public void whenANewDeviceEntersPlacesItsPlayer() throws Exception{
		setListDevices(new UpDevice("Dummy"));
		e.update();
		
		assertThat(e.players()).hasSize(1);
		assertThat(e.players().get(0).id()).isEqualTo(new UUID(0,0));
	}

	@Test public void doesNotAddTheSameDeviceTwice() throws Exception{
		setListDevices(new UpDevice("Dummy"));
		
		executeThisManyTurns(e, 10);
		assertThat(e.players()).hasSize(1);
	}
	
	@Test public void addsPlayersAsThayAppear() throws Exception{
		setListDevices(new UpDevice("Dummy1"));
		executeThisManyTurns(e, 10);
		
		setListDevices(new UpDevice("Dummy1"), new UpDevice("Dummy2"));
		executeThisManyTurns(e, 10);
		assertThat(e.players()).hasSize(2);
	}
	
	@Test public void removesPlayerIfTheyDisconnect() throws Exception{
		setListDevices(new UpDevice("Dummy1"), new UpDevice("Dummy2"));
		executeThisManyTurns(e, 10);
		
		setListDevices(new UpDevice("Dummy1"));
		executeThisManyTurns(e, 10);
		assertThat(e.players()).hasSize(1);
	}
	
	private void setListDevices(UpDevice ... devices) throws Exception {
		when(gateway.listDevices())
			.thenReturn(Arrays.asList(devices));
		int i = 0;
		for(UpDevice d : devices){
			UUID id = new UUID(0,i++);
			Response r = new Response().addParameter("player.id", id.toString());
			whenCallFor(d, "playerInfo").thenReturn(r);
		}
	}

	private OngoingStubbing<Response> whenCallFor(UpDevice d, final String service)
			throws ServiceCallException {
		return when(gateway.callService(eq(d), argThat(new ArgumentMatcher<Call>() {
			public boolean matches(Object arg) {
				return ((Call)arg).getService() == service;
			}
		})));
	}
}
