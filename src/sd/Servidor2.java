package sd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class Servidor2 extends Thread {

	private static Vector CLIENTES;
	private Socket conexao;
	private String nomeCliente;
	private static List LISTA_NOMES_CONECTADOS = new ArrayList();

	public Servidor2(Socket socket) {
		this.conexao = socket;
	}

	/**
	 * testa se nomes são iguais, se for retorna true
	 */
	public boolean armazena(String newName) {

		for (int i = 0; i < LISTA_NOMES_CONECTADOS.size(); i++) {
			if (LISTA_NOMES_CONECTADOS.get(i).equals(newName))
				return true;
		}

		/**
		 * adiciona na lista apenas se não existir
		 */
		LISTA_NOMES_CONECTADOS.add(newName);
		return false;
	}

	/**
	 * remove da lista os CLIENTES que já deixaram o chat
	 * 
	 * @param oldName
	 */
	public void remove(String oldName) {
		for (int i = 0; i < LISTA_NOMES_CONECTADOS.size(); i++) {
			if (LISTA_NOMES_CONECTADOS.get(i).equals(oldName))
				LISTA_NOMES_CONECTADOS.remove(oldName);
		}
	}

	public static void main(String args[]) {

		/**
		 * instancia o vetor de CLIENTES conectados
		 */
		CLIENTES = new Vector();
		try {
			/**
			 * cria um socket que fica escutando a porta 5555.
			 */
			ServerSocket server = new ServerSocket(666);
			System.out.println("Servidor Socket rodando na porta 666");

			while (true) {
				/**
				 * aguarda algum cliente se conectar. A execução do servidor fica bloqueada na
				 * chamada do método accept da classe ServerSocket até que algum cliente se
				 * conecte ao servidor. O próprio método desbloqueia e retorna com um objeto da
				 * classe Socket
				 */
				Socket conexao = server.accept();
				/**
				 * cria uma nova thread para tratar essa conexão
				 */
				Thread t = new Servidor2(conexao);
				t.start();
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}

	public void run() {
		try {
			/**
			 * objetos que permitem controlar fluxo de comunicação que vem do cliente
			 */
			BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream()));

			PrintStream saida = new PrintStream(this.conexao.getOutputStream());
			/**
			 * recebe o nome do cliente
			 */
			this.nomeCliente = entrada.readLine();
			/**
			 * chamada ao metodo que testa nomes iguais
			 */
			if (armazena(this.nomeCliente)) {
				saida.println("Este nome ja existe! Conecte novamente com outro Nome.");
				CLIENTES.add(saida);
				this.conexao.close();
				return;
			} else {
				/**
				 * mostra o nome do cliente conectado ao servidor
				 */
				System.out.println(this.nomeCliente + " : Conectado ao Servidor!");
			}
			if (this.nomeCliente == null) {
				return;
			}
			/**
			 * adiciona os dados de saida do cliente no objeto CLIENTES
			 */
			CLIENTES.add(saida);

			String msg = entrada.readLine();
			/**
			 * Verificar se linha é null (conexão encerrada) Se não for nula, mostra a troca
			 * de mensagens entre os CLIENTES
			 */
			while (msg != null && !(msg.trim().equals(""))) {
				/**
				 * reenvia a linha para todos os CLIENTES conectados
				 */
				sendToAll(saida, " escreveu: ", msg);
				msg = entrada.readLine();
			}

			System.out.println(this.nomeCliente + " saiu do bate-papo!");
			sendToAll(saida, " saiu", " do bate-papo!");
			remove(this.nomeCliente);

			CLIENTES.remove(saida);
			this.conexao.close();
		} catch (IOException e) {
			System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
		}
	}

	/**
	 * enviar uma mensagem para todos, menos para o próprio
	 */
	public void sendToAll(PrintStream saida, String acao, String msg) throws IOException {
		Enumeration e = CLIENTES.elements();
		while (e.hasMoreElements()) {
			/**
			 * obtém o fluxo de saída de um dos CLIENTES
			 */
			PrintStream chat = (PrintStream) e.nextElement();

			/**
			 * envia para todos, menos para o próprio usuário
			 */
			if (chat != saida) {
				chat.println(this.nomeCliente + acao + msg);
			}
		}
	}
}