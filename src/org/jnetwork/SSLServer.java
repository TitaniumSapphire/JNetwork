package org.jnetwork;

import java.io.IOException;
import java.net.SocketException;
import java.security.Security;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import org.jnetwork.listener.TCPConnectionListener;

import com.sun.net.ssl.internal.ssl.Provider;

/**
 * An SSL representation of the Server object. Used for sending and receiving
 * data with SSLConnection objects.
 * 
 * @author Lucas Baizer
 */
public class SSLServer extends TCPServer {
	public SSLServer(Keystore keystore, int port, TCPConnectionListener clientSocketThread) {
		this(keystore, port, Integer.MAX_VALUE, clientSocketThread);
	}

	public SSLServer(Keystore keystore, int port, int maxClients, TCPConnectionListener clientSocketThread) {
		super(port, maxClients, clientSocketThread);

		Security.addProvider(new Provider());

		System.setProperty("javax.net.ssl.keyStore", keystore.getKeystoreLocation().getPath());
		System.setProperty("javax.net.ssl.trustStore", keystore.getKeystoreLocation().getPath());
		System.setProperty("javax.net.ssl.keyStorePassword", keystore.getPassword());
	}

	@Override
	public void start() throws IOException {
		server = SSLServerSocketFactory.getDefault().createServerSocket(getBoundPort());

		super.startDispatch();
	}

	@Override
	protected void launchNewThread() throws IOException, InterruptedException {
		final SSLSocket client;
		try {
			client = (SSLSocket) ((SSLServerSocket) server).accept();
		} catch (SocketException e) {
			if (e.getMessage().equals("socket closed")) {
				return;
			} else {
				throw e;
			}
		}
		// wait until a client disconnects if the maximum amount of
		// clients is full
		// TODO synchronize around object to not chew up CPU
		while (clients.size() == getMaxClients()) {
			Thread.sleep(20);
		}
		final SocketPackage event = new SocketPackage(new SSLConnection(client));

		refresh();
		clients.add(event);
		refresh();

		// sets saved data
		for (SavedData data : savedData)
			if (event.getConnection().getRemoteSocketAddress().toString()
					.equals(data.pkg.getConnection().getRemoteSocketAddress().toString()))
				event.setExtraData(data.data);

		Thread thr = new Thread(new Runnable() {
			@Override
			public void run() {
				((TCPConnectionListener) getClientConnectionListener()).clientConnected(event);
				try {
					removeClient(event);
				} catch (IOException e) {
					Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
				}
			}
		});
		event.setHoldingThread(thr);
		thr.setName("JNetwork-SSLServer-Thread-" + client.getRemoteSocketAddress());
		thr.start();

		launchNewThread();
	}
}
