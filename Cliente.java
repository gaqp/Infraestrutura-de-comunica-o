import java.net.Socket;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Cliente {
	volatile String host;
	volatile int porta;
	volatile String caminho;
	volatile String caminhoSalvar;
	volatile JanelaDownload download;
	Socket dados;
	Socket mensagens;
	InputStream is;
	volatile long offset = 0;
	Thread baixar = new Thread(){
		public void run() {
			try {
				dados = new Socket(host,porta);
				mensagens = new Socket(host, porta);
				dados.setSoTimeout(10000);
				mensagens.setSoTimeout(1000);
				DataOutputStream dosDados;
				DataInputStream disMensagens;
				dosDados = new DataOutputStream(dados.getOutputStream());
				disMensagens = new DataInputStream(dados.getInputStream());
				dosDados.writeUTF(caminho);
				if(disMensagens.readInt()==0) {
					System.out.println("Arquivo não encontrado");
					this.interrupt();
				}else {
					long tamanho = disMensagens.readLong();
					dosDados.writeLong(offset);
					FileOutputStream fos = new FileOutputStream(caminhoSalvar,true);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					is = dados.getInputStream();
					byte[] buffer = new byte [10240];
					int byteLido;
					download.setNomeArquivo(new File(caminhoSalvar).getName());
					Thread velocidade = new Thread() {
						public void run() {
							while(baixar.isAlive()) {
								long medida1 = offset;
								try {
									sleep(1000);
								}catch(Exception e) {
									
								}
								long medida2 = offset;
								long taxa = medida2-medida1;
								download.setRestante((int)((double)((tamanho-medida2)/(double)taxa)));
								download.setTaxa(taxa/1000000);
								System.out.println("Tempo restante atualizado");
							}
							download.setRestante(2147483647);
							System.out.println("Thread finalizada");
							stop();
						}
					};
					velocidade.start();
					while((byteLido = is.read(buffer))!=-1&&!this.isInterrupted()) {
						bos.write(buffer,0,byteLido);
						offset+=byteLido;
						bos.flush();
						download.setProgresso((int)(((double)offset/(double)tamanho)*100));
					}
					fos.close();
					if(this.isInterrupted()) {
					}else {
						download.terminado();
					}
				}
			}catch(Exception e) {
				pausar();
				download.trocar();
				System.out.println("Erro no método cliente baixar: "+e);
			}
		}
	};
a
	public Cliente(String host, int porta, String caminho, String caminhoSalvar) {
		this.host = host;
		this.porta = porta;
		download = new JanelaDownload();
		download.setCliente(this);
		this.caminho = caminho;
		this.caminhoSalvar = caminhoSalvar;
		if(new File(caminhoSalvar).exists()) {
			new File(caminhoSalvar).delete();
		}
		download  = new JanelaDownload();
		download.setCliente(this);
		download.setVisible(true);
		baixar.start();
	}
	public void pausar() {
		baixar.interrupt();
		try {
			dados.close();
			mensagens.close();
			is.close();
		}catch(Exception e) {
			
		}
	}
	public void continuar() {
	baixar.interrupt();
	baixar = new Thread(){
		public void run() {
			try {
				dados = new Socket(host,porta);
				mensagens = new Socket(host, porta);
				dados.setSoTimeout(10000);
				mensagens.setSoTimeout(1000);
				DataOutputStream dosDados;
				DataInputStream disMensagens;
				dosDados = new DataOutputStream(dados.getOutputStream());
				disMensagens = new DataInputStream(dados.getInputStream());
				dosDados.writeUTF(caminho);
				if(disMensagens.readInt()==0) {
					System.out.println("Arquivo não encontrado");
					this.interrupt();
				}else {
					long tamanho = disMensagens.readLong();
					dosDados.writeLong(offset);
					FileOutputStream fos = new FileOutputStream(caminhoSalvar,true);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					is = dados.getInputStream();
					byte[] buffer = new byte [10240];
					int byteLido;
					download.setNomeArquivo(new File(caminhoSalvar).getName());
					Thread velocidade = new Thread() {
						public void run() {
							while(baixar.isAlive()) {
								long medida1 = offset;
								try {
									sleep(1000);
								}catch(Exception e) {
									
								}
								long medida2 = offset;
								long taxa = medida2-medida1;
								download.setRestante((int)((double)((tamanho-medida2)/(double)taxa)));
								download.setTaxa(taxa/1000000);
								System.out.println("Tempo restante atualizado");
							}
							download.setRestante(2147483647);
							System.out.println("Thread finalizada");
							stop();
						}
					};
					velocidade.start();
					while((byteLido = is.read(buffer))!=-1&&!this.isInterrupted()) {
						bos.write(buffer,0,byteLido);
						offset+=byteLido;
						bos.flush();
						download.setProgresso((int)(((double)offset/(double)tamanho)*100));
					}
					fos.close();
					if(this.isInterrupted()) {
					}else {
						download.terminado();
					}
				}
			}catch(Exception e) {
				pausar();
				download.trocar();
				System.out.println("Erro no método cliente baixar: "+e);
			}
		}
	};

	System.out.println("offset :"+offset+" tamanho arquivo: "+new File(caminhoSalvar).length());
	baixar.start();
	}
	public void cancelar() {
		
		baixar.stop();
		download.dispose();
		if(new File(caminhoSalvar).exists()) {
			new File(caminhoSalvar).delete();
		}
	}
}
