import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class ServidorUpload {
	private Socket dados;
	private volatile long atual;
	volatile boolean pong = false;
	JanelaDownload upload = new JanelaDownload();
	private Thread enviar = new Thread() {
		public void run() {
			DataOutputStream dosDados;
			DataInputStream disDados;
			FileInputStream fis;
			BufferedInputStream bis;
			OutputStream os;
			try {
				dados.setSoTimeout(10000);
				upload.setVisible(true);
				upload.upload();
				dosDados = new DataOutputStream (dados.getOutputStream());
				disDados = new DataInputStream (dados.getInputStream());
				String caminho = disDados.readUTF();
				if(!new File(caminho).exists()||new File(caminho).isDirectory()) {
					dosDados.writeInt(0);
					upload.dispose();
					this.stop();
				}else {
					dosDados.writeInt(1);
					long tamanho = new File(caminho).length();
					dosDados.writeLong(tamanho);
					long offset = disDados.readLong();
					int portaUDP = disDados.readInt();
					byte[] buffer;
					int tamanhoBuffer = 1024;
					fis = new FileInputStream(caminho);
					fis.skip(offset);
					bis = new BufferedInputStream(fis);
					os = dados.getOutputStream();
					atual = offset;
					System.out.println("Enviando nome do arquivo");
					dosDados.writeUTF(new File(caminho).getName());
					upload.setNomeArquivo(new File(caminho).getName());
					Thread velocidade = new Thread() {
						public void run() {
							while(enviar.isAlive()) {
								long medida = atual;
								try {
									sleep(1000);
								} catch (InterruptedException e) {
								}
								long medida2 = atual;
								long taxa = medida2-medida;
								upload.setRestante((int)((double)((tamanho-medida2)/(double)taxa)));
								upload.setTaxa(taxa);
							}
							upload.setRestante(2147483647);
							stop();
						} 
					};
					Thread clienteRTT = new Thread() {
						public void run() {
							try {
								try{
								    DatagramSocket serverSocket = new DatagramSocket();
									byte[] recebe = new byte [1];
									byte[] envia = new byte  [1];
									InetAddress ipServer =dados.getInetAddress();
									envia = ("1").getBytes();
									DatagramPacket enviaPacote = new DatagramPacket (envia,envia.length,ipServer,portaUDP);
									while(enviar.isAlive()) {
										long as = System.nanoTime();
										serverSocket.send(enviaPacote);
										DatagramPacket recebePacote = new DatagramPacket(recebe,recebe.length);
										serverSocket.receive(recebePacote);
										upload.setRTT(((double)System.nanoTime() - as)/1000000);
										sleep(500);
									}
									}catch(Exception e){
										System.out.println(e);
									}
								stop();
							} catch (Exception e) {
								System.out.println("ero no clienteRR "+ e);
							}
							
						}
					};
					clienteRTT.start();
					velocidade.start();
					while(atual!=tamanho) {
						if((tamanho-atual)>=tamanhoBuffer) {
							atual+=tamanhoBuffer;
						}else {
							tamanhoBuffer = (int)(tamanho-atual);
							atual = tamanho;
						}
						buffer = new byte[tamanhoBuffer];
						bis.read(buffer,0,tamanhoBuffer);
						os.write(buffer);
						upload.setProgresso((int)(((double)(double)atual/(double)tamanho)*100));
					}
					os.flush();
					os.close();
					fis.close();	
					upload.terminado();
					velocidade.stop();
					upload.dispose();
					this.stop();
				}
			}catch(Exception e) {
				System.out.println("erro ao enviar arquivo: "+e);
				upload.dispose();
				stop();
			}
		}
	};
		public ServidorUpload(Socket dados) {
		this.dados = dados;
		upload.setUpload(this);
		conectar();
	}
	void conectar() {
		enviar.start();
	}
	void parar() {
		upload.dispose();
		enviar.stop();
	}
} 	
