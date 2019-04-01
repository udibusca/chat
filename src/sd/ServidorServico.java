package sd;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import sd.Chat.Acao;

public class ServidorServico {

	/**
	 * crianado a varialvel do Serversocket
	 */
	private ServerSocket serverSocket;
	private Socket socket;
	private Map<String, ObjectOutputStream> usuariosOnline = new HashMap<String, ObjectOutputStream>();

	public ServidorServico() {
		try {
			serverSocket = new ServerSocket(666);
			/**
			 * cria um socket que fica escutando a porta 5555.
			 */
			System.out.println("Servidor Socket rodando na porta 666");

			while (true) {
				/**
				 * aguarda algum cliente se conectar. A execução do servidor fica bloqueada na
				 * chamada do método accept da classe ServerSocket até que algum cliente se
				 * conecte ao servidor. O próprio método desbloqueia e retorna com um objeto da
				 * classe Socket
				 */
				socket = serverSocket.accept();
				/**
				 * cria uma nova thread para tratar essa conexão
				 */
				new Thread(new ListenerSocket(socket)).start();
			}

		} catch (IOException ex) {
			Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private class ListenerSocket implements Runnable {

		private ObjectOutputStream output;
		private ObjectInputStream input;

		public ListenerSocket(Socket socket) {
			try {
				this.output = new ObjectOutputStream(socket.getOutputStream());
				this.input = new ObjectInputStream(socket.getInputStream());
			} catch (IOException ex) {
				Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		@Override
		public void run() {
			Chat message = null;
			try {
				while ((message = (Chat) input.readObject()) != null) {
					Acao acao = message.getAcao();

					if (acao.equals(Acao.CONECTADO)) {
						boolean isConnect = connect(message, output);
						if (isConnect) {
							usuariosOnline.put(message.getName(), output);
							sendOnlines();
						}
					} else if (acao.equals(Acao.DESCONECTADO)) {
						disconnect(message, output);
						sendOnlines();
						return;
					} else if (acao.equals(Acao.ENVIA_INDIVIDUAL)) {
						sendToOne(message);
					} else if (acao.equals(Acao.ENVIA_PARA_ONLINE)) {
						sendAll(message);
					}
				}
			} catch (IOException ex) {
				Chat cm = new Chat();
				cm.setName(message.getName());
				disconnect(cm, output);
				sendOnlines();
				System.out.println(message.getName() + " deixou o chat!");
			} catch (ClassNotFoundException ex) {
				Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	private boolean connect(Chat message, ObjectOutputStream output) {
		if (usuariosOnline.size() == 0) {
			message.setText("YES");
			enviar(message, output);
			return true;
		}

		if (usuariosOnline.containsKey(message.getName())) {
			message.setText("NO");
			enviar(message, output);
			return false;
		} else {
			message.setText("YES");
			enviar(message, output);
			return true;
		}
	}

	private void disconnect(Chat message, ObjectOutputStream output) {
		usuariosOnline.remove(message.getName());

		message.setText(" Até logo!");

		message.setAcao(Acao.ENVIA_INDIVIDUAL);

		sendAll(message);

		System.out.println("User " + message.getName() + " sai da sala");
	}

	private void enviar(Chat message, ObjectOutputStream output) {
		try {
			output.writeObject(message);
		} catch (IOException ex) {
			Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void sendToOne(Chat message) {
		for (Map.Entry<String, ObjectOutputStream> kv : usuariosOnline.entrySet()) {
			if (kv.getKey().equals(message.getNameReserved())) {
				try {
					kv.getValue().writeObject(message);
				} catch (IOException ex) {
					Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	private void sendAll(Chat message) {
		for (Map.Entry<String, ObjectOutputStream> kv : usuariosOnline.entrySet()) {
			if (!kv.getKey().equals(message.getName())) {
				message.setAcao(Acao.ENVIA_INDIVIDUAL);
				try {
					kv.getValue().writeObject(message);
				} catch (IOException ex) {
					Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	private void sendOnlines() {
		Set<String> setNames = new HashSet<String>();
		for (Map.Entry<String, ObjectOutputStream> kv : usuariosOnline.entrySet()) {
			setNames.add(kv.getKey());
		}

		Chat message = new Chat();
		message.setAcao(Acao.ENVIA_PARA_ONLINE);
		message.setSetOnlines(setNames);

		for (Map.Entry<String, ObjectOutputStream> kv : usuariosOnline.entrySet()) {
			message.setName(kv.getKey());
			try {
				kv.getValue().writeObject(message);
			} catch (IOException ex) {
				Logger.getLogger(ServidorServico.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
}
