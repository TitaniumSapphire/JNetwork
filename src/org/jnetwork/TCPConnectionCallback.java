package org.jnetwork;

public interface TCPConnectionCallback extends ClientConnectionCallback {
	/**
	 * This method gets called on when a client connects to the
	 * <code>Server</code>.
	 * 
	 * @param event
	 *            - All the data required to read and write objects with the
	 *            client.
	 */
	public void clientConnected(ClientData event);
}
