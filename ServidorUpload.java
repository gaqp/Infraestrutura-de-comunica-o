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
	private Socket mensagens;
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
				if(!new File(caminho).exists()) {
					dosDados.writeInt(0);
					upload.dispose();
					this.stop();
				}else {
					dosDados.writeInt(1);
					long tamanho = new File(caminho).length();
					dosDados.writeLong(tamanho);
					long offset = disDados.readLong();
					byte[] buffer;
					int tamanhoBuffer = 1024;
					fis = new FileInputStream(caminho);
					fis.skip(offset);
					bis = new BufferedInputStream(fis);
					os = dados.getOutputStream();
					atual = offset;
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
								upload.setTaxa(taxa/1000000);
							}
							upload.setRestante(2147483647);
							stop();
						} 
					};
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
