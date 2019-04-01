package sd;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Chat implements Serializable {
    
	private static final long serialVersionUID = 1L;
	private String name;
    private String text;
    private String nameReserved;
    private Set<String> setOnlines = new HashSet<String>();
    private Acao acao;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameReserved() {
        return nameReserved;
    }

    public void setNameReserved(String nameReserved) {
        this.nameReserved = nameReserved;
    }

    public Set<String> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Set<String> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public Acao getAcao() {
        return acao;
    }

    public void setAcao(Acao action) {
        this.acao = action;
    }
        
    public enum Acao {
        CONECTADO, 
        DESCONECTADO, 
        ENVIA_INDIVIDUAL, 
        ENVIA_TODOS,
        ENVIA_PARA_ONLINE
    }
}
