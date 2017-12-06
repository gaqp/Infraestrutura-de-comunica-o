import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
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
	Janela1 janela;
	Socket dados;
	Socket mensagens;
	InputStream is; 
	volatile long offset = 0;
	volatile boolean pong;
	FileOutputStream fos;
	Thread baixar = new Thread(){
		public void run() {
			try {
				pong = false;
				try {
					dados = new Socket(host,porta);
					mensagens = new Socket(host, porta);
				}catch(UnknownHostException e) {
					janela.showDialogo("Servidor não encontrado");
					download.dispose();
					this.stop();
				}catch(ConnectException e) {
					janela.showDialogo("Não foi possivel conectar");
					download.dispose();
					this.stop();
				}catch(IllegalArgumentException e) {
					janela.showDialogo("Argumento inválido, provavelmente a porta está errada");
					download.dispose();
					this.stop();
				}
				dados.setSoTimeout(10000);
				mensagens.setSoTimeout(10000);
				DataOutputStream dosDados;
				DataInputStream disDados;
				dosDados = new DataOutputStream(dados.getOutputStream());
				disDados = new DataInputStream(dados.getInputStream());
				dosDados.writeUTF(caminho);
				if(disDados.readInt()==0) {
					janela.showDialogo("Arquivo não encontrado");
					download.dispose();
					this.stop();
				}else {
					long tamanho = disDados.readLong();
					dosDados.writeLong(offset);
					fos = new FileOutputStream(caminhoSalvar,true);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
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
							}
							download.setRestante(2147483647);
							try {
								fos.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							stop();
						}
					};
					Thread RTT = new Thread() {
						public void run() {
							while(baixar.isAlive()) {
								try {
									DataOutputStream dosMensagens = new DataOutputStream(mensagens.getOutputStream());
									dosMensagens.writeUTF("PING");
									long tempo = System.nanoTime();
									while(!pong) {
										yield();
									}
									long tempo2 = System.nanoTime();
									download.setRTT(((double)(tempo2  - tempo))/1000000);
									pong = false;
									System.out.println("Cliente RODOU PING");
									sleep(100);
								}catch(Exception e) {
									System.out.println(e);
									try {
										fos.close();
									} catch (IOException e1) {
									}
								}
							}
							stop();
						}
					};
					Thread TrataMensagens = new Thread() {
						public void run() {
							try {
								while(baixar.isAlive()) {
									String mensagem = new DataInputStream(mensagens.getInputStream()).readUTF();
									if(mensagem.equals("PING")) {
										new DataOutputStream(mensagens.getOutputStream()).writeUTF("PONG");
									}else {
										pong = true;
									}
								}
								stop();
							}catch(Exception e) {
								System.out.println(e);
							}
						}
					};
					TrataMensagens.start();
					RTT.start();
					velocidade.start();
					is = dados.getInputStream();
					while((byteLido = is.read(buffer))!=-1&&!this.isInterrupted()) {
						bos.write(buffer,0,byteLido);
						offset+=byteLido;
						bos.flush();
						download.setProgresso((int)(((double)offset/(double)tamanho)*100));
					}
					System.out.println(offset);
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
				pong = false;
				try {
					dados = new Socket(host,porta);
					mensagens = new Socket(host, porta);
				}catch(UnknownHostException e) {
					janela.showDialogo("Servidor não encontrado");
					download.trocar();
					this.stop();
				}catch(ConnectException e) {
					janela.showDialogo("Não foi possivel conectar");
					download.trocar();
					this.stop();
				}catch(IllegalArgumentException e) {
					janela.showDialogo("Argumento inválido, provavelmente a porta está errada");
					download.dispose();
					this.stop();
				}
				dados.setSoTimeout(10000);
				mensagens.setSoTimeout(10000);
				DataOutputStream dosDados;
				DataInputStream disDados;
				dosDados = new DataOutputStream(dados.getOutputStream());
				disDados = new DataInputStream(dados.getInputStream());
				dosDados.writeUTF(caminho);
				if(disDados.readInt()==0) {
					janela.showDialogo("Arquivo não encontrado");
					download.dispose();
					this.stop();
				}else {
					long tamanho = disDados.readLong();
					dosDados.writeLong(offset);
					fos = new FileOutputStream(caminhoSalvar,true);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
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
							}
							download.setRestante(2147483647);
							try {
								fos.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							stop();
						}
					};
					Thread RTT = new Thread() {
						public void run() {
							while(baixar.isAlive()) {
								try {
									DataOutputStream dosMensagens = new DataOutputStream(mensagens.getOutputStream());
									dosMensagens.writeUTF("PING");
									long tempo = System.nanoTime();
									while(!pong) {
										yield();
									}
									long tempo2 = System.nanoTime();
									download.setRTT(((double)(tempo2  - tempo))/1000000);
									pong = false;
									System.out.println("Cliente RODOU PING");
									sleep(100);
								}catch(Exception e) {
									System.out.println(e);
									try {
										fos.close();
									} catch (IOException e1) {
									}
								}
							}
							stop();
						}
					};
					Thread TrataMensagens = new Thread() {
						public void run() {
							try {
								while(baixar.isAlive()) {
									String mensagem = new DataInputStream(mensagens.getInputStream()).readUTF();
									if(mensagem.equals("PING")) {
										new DataOutputStream(mensagens.getOutputStream()).writeUTF("PONG");
									}else {
										pong = true;
									}
								}
								stop();
							}catch(Exception e) {
								System.out.println(e);
							}
						}
					};
					TrataMensagens.start();
					RTT.start();
					velocidade.start();
					is = dados.getInputStream();
					while((byteLido = is.read(buffer))!=-1&&!this.isInterrupted()) {
						bos.write(buffer,0,byteLido);
						offset+=byteLido;
						bos.flush();
						download.setProgresso((int)(((double)offset/(double)tamanho)*100));
					}
					System.out.println(offset);
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
	
	baixar.start();
	}
	public void cancelar() {
		try {
			dados.close();
			mensagens.close();
			baixar.interrupt();
			fos.close();
			download.dispose();
			new File(caminhoSalvar).delete();
			boolean ping = new File(caminhoSalvar).exists();
			System.out.println("Arquivo apagado "+ping);
		}catch(Exception e) {
			download.dispose();
			new File(caminhoSalvar).delete();
		}
	}
	public void setJanela(Janela1 janela) {
		this.janela = janela;
	}
	public void parar() {
		baixar.interrupt();
		download.dispose();
	}
}
