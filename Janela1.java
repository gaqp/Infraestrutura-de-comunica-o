import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import java.awt.Choice;
import javax.swing.JComboBox;

public class Janela1 extends JFrame {

	private JPanel contentPane;
	private JLabel IP;
	private JLabel Porta;
	private JTextField portaDestino;
	private JTextField ipDestino;
	private JTextField CaminhoSalvar;
	private Servidor servidor;
	private Janela1 janela;
	public JOptionPane dialogo;
	private JLabel pastaLabel;
	public String pastaCompartilhadaCaminho = "";
	private JComboBox CaminhoServidor;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Janela1 frame = new Janela1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Janela1() {
		janela = this;
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 680, 544);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblMeuIp = new JLabel("IP: ");
		lblMeuIp.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblMeuIp.setBounds(10, 457, 53, 38);
		contentPane.add(lblMeuIp);

		IP = new JLabel("IP HERE");
		IP.setFont(new Font("Tahoma", Font.PLAIN, 14));
		IP.setBounds(59, 462, 233, 28);
		contentPane.add(IP);

		JLabel lblMinhaPorta = new JLabel("Porta: ");
		lblMinhaPorta.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblMinhaPorta.setBounds(321, 457, 92, 38);
		contentPane.add(lblMinhaPorta);

		Porta = new JLabel("Porta Here");
		Porta.setFont(new Font("Tahoma", Font.PLAIN, 14));
		Porta.setBounds(423, 457, 188, 38);
		contentPane.add(Porta);

		JLabel lblDigiteOCaminho = new JLabel("Digite o  caminho do arquivo a ser baixado do servidor");
		lblDigiteOCaminho.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDigiteOCaminho.setBounds(10, 11, 501, 28);
		contentPane.add(lblDigiteOCaminho);

		JLabel lblDigiteOCaminho_1 = new JLabel("Digite o caminho para salvar o arquivo ou procure o caminho");
		lblDigiteOCaminho_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDigiteOCaminho_1.setBounds(10, 165, 448, 38);
		contentPane.add(lblDigiteOCaminho_1);

		CaminhoSalvar = new JTextField();
		CaminhoSalvar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		CaminhoSalvar.setBounds(10, 202, 511, 38);
		contentPane.add(CaminhoSalvar);
		CaminhoSalvar.setColumns(10);

		JButton Baixar = new JButton("Baixar");
		Baixar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (caminhoSalvar().equals("") || caminhoSalvar().equals(null) || caminhoServidor().equals(null)
						|| caminhoServidor().equals("")) {
					showDialogo("O caminho não pode ser vazio");
				} try {
					if(new File(caminhoServidor()).createNewFile()) {
						showDialogo("O caminho não pode ser uma pasta");
					}else {
						try {
							Paths.get(CaminhoSalvar.getText());
							Cliente cliente = new Cliente(ipDestino(), Integer.parseInt(portaDestino()), caminhoServidor(), caminhoSalvar(),1);
							setJanelaCliente(cliente);
						} catch (IllegalArgumentException e) {
							showDialogo("Erro" + e);
						}

					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Baixar.setBounds(273, 405, 89, 23);
		contentPane.add(Baixar);

		JLabel lblDigiteOIp = new JLabel("Digite o ip do destino:");
		lblDigiteOIp.setBounds(10, 331, 214, 14);
		contentPane.add(lblDigiteOIp);

		ipDestino = new JTextField();
		ipDestino.setBounds(10, 356, 335, 20);
		contentPane.add(ipDestino);
		ipDestino.setColumns(10);

		JLabel lblDigiteAPorta = new JLabel("Digite a porta do destino:");
		lblDigiteAPorta.setBounds(355, 331, 222, 14);
		contentPane.add(lblDigiteAPorta);

		portaDestino = new JTextField();
		portaDestino.setBounds(355, 356, 299, 20);
		contentPane.add(portaDestino);
		portaDestino.setColumns(10);

		JButton btnProcurar = new JButton("Procurar");
		btnProcurar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser seletor = new JFileChooser();
				seletor.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int retorno = seletor.showSaveDialog(seletor);
				if (retorno == JFileChooser.APPROVE_OPTION) {
					String caminho = seletor.getSelectedFile().getPath();
					CaminhoSalvar.setText(caminho);
				} else {
					showDialogo("Caminho não selecionado");
				}
			}
		});
		btnProcurar.setBounds(540, 211, 89, 23);
		contentPane.add(btnProcurar);

		JButton btnCompartilharPasta = new JButton("Compartilhar pasta");
		btnCompartilharPasta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (btnCompartilharPasta.getText().equals("Compartilhar pasta")) {
					JFileChooser seletor = new JFileChooser();
					seletor.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int retorno = seletor.showSaveDialog(seletor);
					if (retorno == JFileChooser.APPROVE_OPTION) {
						String caminho = seletor.getSelectedFile().getPath();
						pastaCompartilhadaCaminho = caminho;
						setPasta();
						btnCompartilharPasta.setText("Parar de compartilhar");
					}
				} else {
					pastaCompartilhadaCaminho = "";
					setPasta();
					btnCompartilharPasta.setText("Compartilhar pasta");
					System.out.println("Botão apertado");
				}
			}
		});
		btnCompartilharPasta.setBounds(493, 297, 171, 23);
		contentPane.add(btnCompartilharPasta);

		pastaLabel = new JLabel("Pasta Compartilhada: ");
		pastaLabel.setBounds(10, 301, 511, 14);
		contentPane.add(pastaLabel);
		
		JButton btnReceberLista = new JButton("Receber lista");
		btnReceberLista.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!ipDestino().equals("")) {
					try {
						int teste= Integer.parseInt(portaDestino());
						Cliente cliente = new Cliente(ipDestino(), Integer.parseInt(portaDestino()),"nenhum", "nada",0);
						setJanelaCliente(cliente);
					}catch(Exception e) {
						showDialogo("Erro ao tentar converter porta ");
						e.printStackTrace();
					}
				}
				
			}
		});
		btnReceberLista.setBounds(531, 55, 133, 23);
		contentPane.add(btnReceberLista);
		
		CaminhoServidor = new JComboBox();
		CaminhoServidor.setEditable(true);
		CaminhoServidor.setBounds(10, 50, 511, 32);
		contentPane.add(CaminhoServidor);
		servidor = new Servidor(1000 + new Random().nextInt(8999), this);
		this.setTitle("TRANSFER");
		dialogo = new JOptionPane();
		CaminhoServidor.setSelectedItem("");
	}

	public void SetPortaIP(String ip, int porta) {
		Porta.setText(porta + "");
		IP.setText(ip);
	}

	public String ipDestino() {
		return ipDestino.getText();
	}

	public String caminhoServidor() {
		String caminho = this.CaminhoServidor.getSelectedItem().toString();
		// CaminhoServidor.setText("");
		return caminho;

	}

	public String caminhoSalvar() {
		String caminho = CaminhoSalvar.getText();
		// CaminhoSalvar.setText("");
		return caminho;
	}

	public String portaDestino() {
		return portaDestino.getText();
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public void setSalvar(String caminho) {
		this.CaminhoSalvar.setText(caminho);
	}

	public void showDialogo(String dialogo) {
		this.dialogo.showMessageDialog(this, dialogo);
	}

	public void setJanelaCliente(Cliente cliente) {
		cliente.setJanela(this);
	}

	public void setPasta() {
		pastaLabel.setText("Pasta Compartilhada: " + pastaCompartilhadaCaminho);
	}
	public int getPortaUDP() {
		return servidor.getPorta();
	}
	public void setLista(String [] array) {
		this.CaminhoServidor.removeAllItems();
		for(int i = 0;i<array.length;i++) {
			this.CaminhoServidor.addItem(array[i]);
		}
	}
}
