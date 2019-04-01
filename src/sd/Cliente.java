package sd;

public class Cliente {

	public static void main(String args[]) {
		/**
		 * Criando uma nova instancia, chamando o Cliente.
		 */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new ClienteFrame().setVisible(true);
			}
		});
	}
}
