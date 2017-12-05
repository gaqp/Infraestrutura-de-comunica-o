import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Servidor {
	ServerSocket servidor;
	Janela1 janela;
	public Servidor(int porta, Janela1 janela) {
		try {
			servidor = new ServerSocket(porta);
			this.janela = janela;
			janela.setServidor(this);
			janela.SetPortaIP(InetAddress.getLocalHost().getHostAddress(), porta);
					new Thread() {
						public void run() {
							while (true) {
								try {
									Socket socket = servidor.accept();
									Socket mensagens = servidor.accept();
									cliente(socket, mensagens);
								} catch (Exception e) {
									System.out.println("Erro ao conectar cliente: " + e);
								}
							}
						}
					}.start();

		} catch (Exception e) {
			System.out.println("Erro no construtor do servidor: " + e);
		}
	}
	private  void cliente(Socket socket, Socket mensagens) {
				new ServidorUpload(socket,mensagens);
	}
	public int getPorta() {
		return servidor.getLocalPort();
	}
	public String getIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			System.out.print("ERRO AO PEGAR IP");
		}
		return "localhost";
	}
}
