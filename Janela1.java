import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;

public class Janela1 extends JFrame {

	private JPanel contentPane;
	private  JLabel IP;
	private  JLabel Porta;
	private  JTextField portaDestino;
	private  JTextField ipDestino;
	private  JTextField CaminhoServidor;
	private  JTextField CaminhoSalvar;
	private  Servidor servidor;
	private Janela1 janela;
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
		
		CaminhoServidor = new JTextField();
		CaminhoServidor.setFont(new Font("Tahoma", Font.PLAIN, 14));
		CaminhoServidor.setBounds(10, 100, 644, 38);
		contentPane.add(CaminhoServidor);
		CaminhoServidor.setColumns(10);
		
		JLabel lblDigiteOCaminho = new JLabel("Digite o  caminho do arquivo a ser baixado do servidor");
		lblDigiteOCaminho.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDigiteOCaminho.setBounds(157, 61, 354, 28);
		contentPane.add(lblDigiteOCaminho);
		
		JLabel lblDigiteOCaminho_1 = new JLabel("Digite o caminho para salvar o arquivo");
		lblDigiteOCaminho_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDigiteOCaminho_1.setBounds(197, 149, 261, 54);
		contentPane.add(lblDigiteOCaminho_1);
		
		CaminhoSalvar = new JTextField();
		CaminhoSalvar.setFont(new Font("Tahoma", Font.PLAIN, 14));
		CaminhoSalvar.setBounds(10, 202, 555, 38);
		contentPane.add(CaminhoSalvar);
		CaminhoSalvar.setColumns(10);
		
		JButton Baixar = new JButton("Baixar");
		Baixar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Cliente(ipDestino(),portaDestino(),caminhoServidor(),caminhoSalvar());
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
		
		JButton procurar = new JButton("Procurar");
		procurar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EscolherArquivo ea = new EscolherArquivo();
				ea.setVisible(true);
				ea.setJanela(janela);
			}
		});
		procurar.setBounds(575, 211, 89, 23);
		contentPane.add(procurar);
		
		JButton local = new JButton("Set Localhost");
		local.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ipDestino.setText(servidor.getIP());
				portaDestino.setText(servidor.getPorta()+"");
				CaminhoServidor.setText("C:\\Users\\Gabriel Alves\\Pictures\\Saved Pictures\\1840Megas.7z.001");
				CaminhoSalvar.setText("C:\\Users\\Gabriel Alves\\Pictures\\Saved Pictures\\1840Megas.7z.002");
			}
		});
		local.setBounds(259, 297, 117, 23);
		contentPane.add(local);
		servidor = new Servidor(1000+new Random().nextInt(8999),this);	
		this.setTitle("TRANSFER"); 
	}
	public   void SetPortaIP(String ip, int porta) {
		Porta.setText(porta+"");
		IP.setText(ip);
	}
	public  String ipDestino() {
		return ipDestino.getText();
	}
	public  String caminhoServidor() {
		String caminho = CaminhoServidor.getText();
		//CaminhoServidor.setText("");
		return caminho;
		
	}
	public  String caminhoSalvar() {
		String caminho = CaminhoSalvar.getText();
		//CaminhoSalvar.setText("");
		return caminho;
	}
	public  int portaDestino() {
		return Integer.parseInt(portaDestino.getText());
	}
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}
	public  void setSalvar(String caminho) {
		this.CaminhoSalvar.setText(caminho);
	}
}
