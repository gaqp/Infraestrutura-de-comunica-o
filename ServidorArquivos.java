import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ServidorArquivos {
	private Socket dados;
	private Socket mensagens;
	private DataInputStream dis;
	private DataOutputStream dos;
	private listaArquivos arquivosCaminho;
	public ServidorArquivos() {
		arquivosCaminho = new listaArquivos();
	}
	public void inserirArquivo(String caminho) {
		arquivosCaminho.inserir(caminho);
		System.out.println("Arquivo inserido: "+caminho);
	}
	public boolean removerArquivo(String caminho) {
		return arquivosCaminho.remover(caminho);
	}
	public void imprimir() {
		arquivosCaminho.imprimir();
	}
}
class listaArquivos{
	String caminho;
	listaArquivos proximo;
	public listaArquivos() {
		
	}
	public void inserir(String caminho) {
		if(this.caminho==null) {
			this.caminho = caminho;
			
		}else {
			this.proximo.inserir(caminho);
		}if(this.proximo == null) {
			this.proximo = new listaArquivos();
		}
		

	}
	public boolean remover(String caminho) {
		if(this.caminho.equals(caminho)) {
			this.caminho = null;
			return true;
		}else if(this.proximo!=null) {
			return this.proximo.remover(caminho);
		}else return false;
	}
	public void imprimir() {
		if(this.caminho!=null) {
			System.out.println(caminho);
		}
		if(this.proximo!=null) {
			this.proximo.imprimir();
		}
	}
}
