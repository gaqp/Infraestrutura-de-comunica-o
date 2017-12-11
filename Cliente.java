import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.DatagramPacket;

public class Cliente {
	volatile String host;
	volatile int porta;
	volatile String caminho;
	volatile String caminhoSalvar;
	volatile JanelaDownload download;
	Janela1 janela;
	Socket dados;
	InputStream is; 
	volatile long offset = 0;
	volatile boolean pong;
	FileOutputStream fos = null;
	Thread baixar = new Thread(){
		public void run() {
			try {
				pong = false;
				try {
					dados = new Socket(host,porta);
				}catch(UnknownHostException e) {
					janela.showDialogo("Servidor não encontrado");
					download.dispose();
					this.stop();
				}catch(ConnectException e) {
					janela.showDialogo("Não foi possivel conectar");
					download.dispose();
					this.stop();
				}catch(IllegalArgumentException e) {
					janela.showDialogo("Argumento inválido, porta está errada");
					download.dispose();
					this.stop();
				}
				dados.setSoTimeout(10000);
				DataOutputStream dosDados;
				DataInputStream disDados;
				dosDados = new DataOutputStream(dados.getOutputStream());
				disDados = new DataInputStream(dados.getInputStream());
				dosDados.writeInt(1);
				dosDados.writeUTF(caminho);
				if(disDados.readInt()==0) {
					janela.showDialogo("Arquivo não encontrado");
					download.dispose();
					dados.close();
					this.stop();
				}else {
					long tamanho = disDados.readLong();
					dosDados.writeLong(offset);
					dosDados.writeInt(janela.getPorta());
					String nomeArquivo = disDados.readUTF();
					download.setNomeArquivo(nomeArquivo);
					String barra =  "\\";
					String caminhoOriginal = caminhoSalvar;
					caminhoSalvar += barra+nomeArquivo;
					System.out.println(caminhoSalvar);
					int i = 0;
					while(new File(caminhoSalvar).exists()) {
						caminhoSalvar = caminhoOriginal+barra+i+nomeArquivo;
						i++;
					}
					fos = new FileOutputStream(caminhoSalvar,true);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					byte[] buffer = new byte [1024];
					int byteLido;
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
								download.setTaxa(taxa);
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
					velocidade.start();
					Thread clienteRTT = new Thread() {
						public void run() {
							try {
								try{
								    DatagramSocket serverSocket = new DatagramSocket();
									byte[] recebe = new byte [1];
									byte[] envia = new byte  [1];
									InetAddress ipServer = InetAddress.getByName(host);
									envia = ("1").getBytes();
									DatagramPacket enviaPacote = new DatagramPacket (envia,envia.length,ipServer,porta);
									while(baixar.isAlive()) {
										long as = System.nanoTime();
										serverSocket.send(enviaPacote);
										DatagramPacket recebePacote = new DatagramPacket(recebe,recebe.length);
										serverSocket.setSoTimeout(2000);
										boolean timeout;
										try {
											timeout =false;
											serverSocket.receive(recebePacote);
										}catch(Exception e) {
											timeout = true;
											System.out.println("Timeout ping");
										}
										if(!timeout) {
											download.setRTT(((double)System.nanoTime() - as)/1000000);
											sleep(500);
										}	
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
					is = dados.getInputStream();
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
 
	public Cliente(String host, int porta, String caminho, String caminhoSalvar, int opcao, Janela1 Janela) {
		this.janela = Janela;
		if(opcao == 1) {
			this.host = host;
			this.porta = porta;
			download = new JanelaDownload();
			download.setCliente(this);
			this.caminho = caminho;
			this.caminhoSalvar = caminhoSalvar;
			if(!new File(caminhoSalvar).isDirectory()) {
				
			}else {
				download  = new JanelaDownload();
				download.setCliente(this);
				download.setVisible(true);
				baixar.start();
			}
			
		}else {
			new Thread() {
				public void run() {
					try {
						Socket dados = new Socket(host, porta);
						new DataOutputStream(dados.getOutputStream()).writeInt(0);
						DataInputStream entrada = new DataInputStream(dados.getInputStream());
						if(entrada.readInt()==0) {
							
						}else {
							int tamanho = entrada.readInt();
							System.out.println("tamanho "+tamanho);
							String [] arquivos = new String [tamanho+1];
							for(int i = 1;i<=tamanho;i++) {
								arquivos[i] = entrada.readUTF();
							}
							janela.setLista(arquivos);
							System.out.println("Finalizado");
							stop();
						}
					} catch (UnknownHostException e) {
						janela.showDialogo("Host não encontrado");
						stop();
					} catch (IOException e) {
						janela.showDialogo("Erro de I/O");
						stop();
					}
				}
			}.start();
		}
	}
	public void pausar() {
		baixar.interrupt();
		try {
			dados.close();
			fos.close();
			is.close();
		}catch(Exception e) {
			
		}
	}
	public void continuar() {
	baixar.stop();
	baixar = new Thread(){
		public void run() {
			try {
				pong = false;
				try {
					dados = new Socket(host,porta);
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
					download.trocar();
					this.stop();
				}catch(Exception e) {
					janela.showDialogo("Erro: "+e);
					download.trocar();
					this.stop();
				}
				dados.setSoTimeout(10000);
				DataOutputStream dosDados;
				DataInputStream disDados;
				dosDados = new DataOutputStream(dados.getOutputStream());
				disDados = new DataInputStream(dados.getInputStream());
				dosDados.writeInt(1);
				dosDados.writeUTF(caminho);
				if(disDados.readInt()==0) {
					janela.showDialogo("Arquivo não encontrado");
					download.dispose();
					this.stop();
				}else {
					long tamanho = disDados.readLong();
					dosDados.writeLong(offset);
					dosDados.writeInt(janela.getPorta());
					String nomeArquivo = disDados.readUTF();
					download.setNomeArquivo(nomeArquivo);
					System.out.println(caminhoSalvar);
					fos = new FileOutputStream(caminhoSalvar,true);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					byte[] buffer = new byte [1024];
					int byteLido;
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
								download.setTaxa(taxa);
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
					velocidade.start();
					Thread clienteRTT = new Thread() {
						public void run() {
							try {
								try{
								    DatagramSocket serverSocket = new DatagramSocket();
									byte[] recebe = new byte [1];
									byte[] envia = new byte  [1];
									InetAddress ipServer = InetAddress.getByName(host);
									envia = ("1").getBytes();
									DatagramPacket enviaPacote = new DatagramPacket (envia,envia.length,ipServer,porta);
									while(baixar.isAlive()) {
										long as = System.nanoTime();
										serverSocket.send(enviaPacote);
										DatagramPacket recebePacote = new DatagramPacket(recebe,recebe.length);
										serverSocket.setSoTimeout(2000);
										boolean timeout;
										try {
											timeout =false;
											serverSocket.receive(recebePacote);
										}catch(Exception e) {
											timeout = true;
											System.out.println("Timeout ping");
										}
										if(!timeout) {
											download.setRTT(((double)System.nanoTime() - as)/1000000);
											sleep(500);
										}	
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
				try {
					fos.close();
				} catch (IOException e1) {
					
				}
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
		try {
			dados.close();
			fos.close();
			is.close();
		} catch (IOException e) {
			
		}
		download.dispose();
	}
}
