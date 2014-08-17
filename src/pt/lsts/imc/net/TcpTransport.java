package pt.lsts.imc.net;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pt.lsts.imc.Abort;
import pt.lsts.imc.IMCDefinition;
import pt.lsts.imc.IMCInputStream;
import pt.lsts.imc.IMCMessage;
import pt.lsts.imc.IMCOutputStream;

public class TcpTransport {

	protected boolean bound = false;
	protected ServerSocket socket;
	protected ExecutorService executor = Executors.newCachedThreadPool();

	protected void dispatch(IMCMessage msg) {
		//msg.dump(System.err);
	}

	public void shutdown() {
		bound = false;
		executor.shutdown();		
		try {
			socket.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Future<Boolean> send(final String host, final int port, final IMCMessage msg) {		
		return executor.submit(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				Socket socket = new Socket(host, port);
				socket.setSoTimeout(10000);
				IMCOutputStream ios = new IMCOutputStream(socket.getOutputStream());
				msg.serialize(ios);
				socket.close();
				return true;
			}
		});
	}

	public void bind(int port) throws Exception {
		bound = true;
		socket = new ServerSocket(port);
		Thread serverSocket = new Thread("TCP Server") {
			public void run() {
				while (bound) {
					try {
						Socket connection = socket.accept();
						ClientHandler handler = new ClientHandler(TcpTransport.this, connection);
						handler.start();
					}
					catch (Exception e) {

					}
				}
				try {
					socket.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			};
		};
		serverSocket.start();
	}

	class ClientHandler extends Thread {

		protected TcpTransport transport;
		protected Socket clientConnection;
		protected IMCInputStream input;
		public ClientHandler(TcpTransport transport, Socket clientConnection) throws IOException {
			this.transport = transport;
			this.clientConnection = clientConnection;
			input = new IMCInputStream(clientConnection.getInputStream(), IMCDefinition.getInstance());
		}

		@Override
		public void run() {
			while (!clientConnection.isClosed()) {
				try {
					IMCMessage msg = IMCDefinition.getInstance().nextMessage(input);
					transport.dispatch(msg);
				}
				catch (EOFException e) {
					try {
						clientConnection.close();						
					}
					catch (Exception ex) {
						ex.printStackTrace();
					}
					return;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		TcpTransport transport = new TcpTransport();
		transport.bind(9001);
		long start = System.currentTimeMillis();
		int count = 0;
		while(System.currentTimeMillis() - start < 1000) {
			transport.send("localhost", 9001, new Abort());
			count++;
		}
		System.out.println(count);
		transport.shutdown();
	}
}
