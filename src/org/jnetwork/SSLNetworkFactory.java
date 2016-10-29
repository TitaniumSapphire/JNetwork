package org.jnetwork;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jnetwork.listener.ClientConnectionListener;
import org.jnetwork.listener.TCPConnectionListener;

/**
 * A factory for creating SSL sockets.
 * 
 * @author Lucas Baizer
 */
public class SSLNetworkFactory extends NetworkFactory {
	SSLNetworkFactory() {
	}

	@Override
	public Connection createConnection(String host, int port) throws UnknownHostException, IOException {
		return new SSLConnection(host, port);
	}

	@Override
	public Server createServer(int port, ClientConnectionListener l) throws ServerException {
		return new SSLServer(null, port, (TCPConnectionListener) l);
	}
}
