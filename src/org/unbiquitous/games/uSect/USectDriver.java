package org.unbiquitous.games.uSect;

import java.util.List;

import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uos.core.InitialProperties;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.applicationManager.CallContext;
import org.unbiquitous.uos.core.driverManager.UosDriver;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDriver;
import org.unbiquitous.uos.core.messageEngine.messages.Call;
import org.unbiquitous.uos.core.messageEngine.messages.Response;

public class USectDriver implements UosDriver {

	private GameSettings settings;

	public USectDriver(GameSettings settings) {
		this.settings = settings;
	}

	@Override
	public UpDriver getDriver() {
		return new UpDriver("usect.driver");
	}

	@Override
	public List<UpDriver> getParent() {
		return null;
	}

	@Override
	public void init(Gateway gateway, InitialProperties properties,
			String instanceId) {

	}

	public void playerInfo(Call call, Response response, CallContext ctx){
		response.addParameter("player.id",settings.get("usect.player.id"));
	}
	
	@Override
	public void destroy() {
	}

}
