package ca.ualberta.library.ir.fedora.security;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TrustingSSLSocketFactory extends SocketFactory {

	private SSLSocketFactory factory;

	TrustManager[] trustingTrustMan = new TrustManager[] { new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] c, String a) {
			return;
		}

		public void checkServerTrusted(X509Certificate[] c, String a) {
			return;
		}
	} };

	public TrustingSSLSocketFactory() {
		try {
			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, trustingTrustMan, new java.security.SecureRandom());
			factory = sslcontext.getSocketFactory();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static SocketFactory getDefault() {
		return new TrustingSSLSocketFactory();
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
		return factory.createSocket(host, port);
	}

	@Override
	public Socket createSocket(InetAddress address, int port) throws IOException {
		return factory.createSocket(address, port);
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localAddress, int localPort) throws IOException,
		UnknownHostException {
		return factory.createSocket(host, port, localAddress, localPort);
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
		throws IOException {
		return factory.createSocket(address, port, localAddress, localPort);
	}

	public String[] getDefaultCipherSuites() {
		return factory.getSupportedCipherSuites();
	}

	public String[] getSupportedCipherSuites() {
		return factory.getSupportedCipherSuites();
	}

}
