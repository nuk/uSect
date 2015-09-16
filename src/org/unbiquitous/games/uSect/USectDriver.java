package org.unbiquitous.games.uSect;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.unbiquitous.games.uSect.environment.Environment;
import org.unbiquitous.games.uSect.objects.Player;
import org.unbiquitous.games.uSect.objects.Sect;
import org.unbiquitous.uImpala.engine.core.GameSettings;
import org.unbiquitous.uos.core.InitialProperties;
import org.unbiquitous.uos.core.UOSLogging;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.applicationManager.CallContext;
import org.unbiquitous.uos.core.driverManager.UosDriver;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDriver;
import org.unbiquitous.uos.core.messageEngine.messages.Call;
import org.unbiquitous.uos.core.messageEngine.messages.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class USectDriver implements UosDriver {
	private static final Logger LOGGER = UOSLogging.getLogger();
	private static final ObjectMapper mapper = new ObjectMapper();

	private GameSettings settings;
	private Environment e;

	public USectDriver(GameSettings settings, Environment e) {
		this.settings = settings;
		this.e = e;
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
	public void init(Gateway gateway, InitialProperties properties, String instanceId) {

	}

	public void connect(Call call, Response response, CallContext ctx) {
		response.addParameter("player.id", settings.get("usect.player.id"));
		LOGGER.fine("Connecting " + ctx.getCallerDevice());
		e.players().get(0).connect(ctx.getCallerDevice());
	}

	@Override
	public void destroy() {
	}

	public void call(Call call, Response response, CallContext ctx) {
		UUID id = UUID.fromString(call.getParameterString("id"));
		LOGGER.info("Received call for Player " + id);
		for (Player p : e.players()) {
			if (id.equals(p.id())) {
				p.call();
				return;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void migrate(Call call, Response response, CallContext ctx) {
		ObjectNode json = mapper.valueToTree((Map) call.getParameter("sect"));
		Sect s = Sect.fromJSON(e, json);
		LOGGER.info("Received Sect " + s);
	}
}
