package de.syscy.warpsystem.transfer;

import com.velocitypowered.api.proxy.ProxyServer;

public interface DataHandler {
	String GET_CHANNEL = "warpsystem:get";
	String REQUEST_CHANNEL = "warpsystem:request";

	void onEnable(ProxyServer proxy);

	void onDisable(ProxyServer proxy);
}
