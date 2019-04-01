package sd;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClienteService {
    
    private Socket socket;
    private ObjectOutputStream output;
    
    public Socket connect() {
        try {
			/**
			 * Instancia do atributo conexao do tipo Socket, conecta a IP do Servidor, Porta
			 */
            this.socket = new Socket("localhost", 666);
            
            this.output = new ObjectOutputStream(socket.getOutputStream());
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return socket;
    }
    
    public void send(Chat message) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
