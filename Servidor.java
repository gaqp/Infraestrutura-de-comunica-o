import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
					Thread  cliente = new Thread() {
						public void run() {
							while (true) {
								try {
									Socket socket = servidor.accept();
									DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
									if(new DataInputStream(socket.getInputStream()).readInt()==1) {
										cliente(socket);
									}else {
												try { 
													System.out.println("arquivos solicitados");
													String caminho = janela.pastaCompartilhadaCaminho;
													if(!caminho.equals("")) {
														dos.writeInt(1);
														File [] arquivos = new File(caminho).listFiles(new FileFilter() {
															public boolean accept(File caminho) {
																return caminho.isFile();
															}
														});
														dos.writeInt(arquivos.length);
														for(int i = 0;i<arquivos.length;i++) {
															dos.writeUTF(arquivos[i].getAbsolutePath());
														}
													}else {
														dos.writeInt(0);
													}
												}catch(Exception e) {
													System.out.println("Erro ao tentar compartilhar pasta "+e);
												}
									}
								} catch (Exception e) {
									System.out.println("Erro ao conectar cliente: " + e);
									e.printStackTrace();
								}
							}
						}
					};
					cliente.start();
					Thread udpRTT = new Thread() {
						public void run() {
							try {
								DatagramSocket servidorUDP = new DatagramSocket(porta);
								byte[] buffer = new byte[1];
								byte[] bufferResposta = new byte[1];
								bufferResposta = ("2").getBytes();
								while(cliente.isAlive()) {
									DatagramPacket pacoteRecebido = new DatagramPacket(buffer,buffer.length);
									System.out.println("Servidor: Aguardado pacote, porta servidor é: "+servidorUDP.getLocalPort());
									servidorUDP.receive(pacoteRecebido);
									System.out.println("Servidor: Pacote Recebido");
									InetAddress ipCliente = pacoteRecebido.getAddress();
									int portaCliente = pacoteRecebido.getPort();
									DatagramPacket enviarPacote = new DatagramPacket(bufferResposta,bufferResposta.length,ipCliente,portaCliente);
									servidorUDP.send(enviarPacote);
								}
							} catch (Exception e) {
								System.out.println("Erro no servidor UDP "+e);
							}
						}
					};
					udpRTT.start();
		} catch (Exception e) {
			System.out.println("Erro no construtor do servidor: " + e);
		}
	}
	private  void cliente(Socket socket) {
				new ServidorUpload(socket);
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
