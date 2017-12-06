import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.awt.event.ActionEvent;

public class JanelaDownload extends JFrame {

	private JPanel contentPane;
	private  JLabel RTT;
	private  JProgressBar Progresso;
	private  JLabel Taxa;
	private Cliente cliente;
	private ServidorUpload upload;
	private JLabel restante;
	private JButton cancelar;
	private JButton pausar;
	private JLabel parte;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JanelaDownload frame = new JanelaDownload();
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
	public JanelaDownload() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 321, 190);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		this.setLocationRelativeTo(null);
		RTT = new JLabel("RTT: ");
		RTT.setBounds(10, 113, 237, 14);
		contentPane.add(RTT);
		
		Progresso = new JProgressBar();
		Progresso.setStringPainted(true);
		Progresso.setBounds(10, 34, 287, 14);
		contentPane.add(Progresso);
		
		pausar = new JButton("Pausar");
		pausar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(pausar.getText().equals("Pausar")) {
					pausar();
					new Thread() {
						public void run() {
							try {
								pausar.setEnabled(false);
								sleep(500);
								pausar.setEnabled(true);

							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					pausar.setText("Continuar");
					
				}else {
					continuar();
					new Thread() {
						public void run() {
							try {
								pausar.setEnabled(false);
								sleep(250);
								pausar.setEnabled(true);

							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
					pausar.setText("Pausar");
				}
			}
		});
		pausar.setBounds(10, 59, 89, 23);
		contentPane.add(pausar);
		
		cancelar = new JButton("Cancelar");
		cancelar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					cancelar();
				} catch (IOException e1) {
					
				}	
			}
		});
		cancelar.setBounds(109, 59, 89, 23);
		contentPane.add(cancelar);
		
		Taxa = new JLabel("Velocidade: ");
		Taxa.setBounds(10, 137, 287, 14);
		contentPane.add(Taxa);
		
		restante = new JLabel("Tempo restante: ");
		restante.setBounds(10, 93, 287, 14);
		contentPane.add(restante);
		
		parte = new JLabel("");
		parte.setBounds(10, 9, 305, 14);
		contentPane.add(parte);
		
		JButton fechar = new JButton("Fechar");
		fechar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(cliente!=null) {
					cliente.parar();
					dispose();
				}
				if(upload!=null) {
					upload.parar();
					dispose();
				}
			}
		});
		fechar.setBounds(220, 59, 89, 23);
		contentPane.add(fechar);
		this.setTitle("Baixando Arquivo");
		
	}
	public  void setRTT(double ping) {
		RTT.setText("RTT: "+ String.format("%.2f", ping) +" ms"); 
	}
	public  void setTaxa(long taxa) {
			Taxa.setText("Taxa: "+taxa+" MBps");	
	}
	public  void setProgresso(int progresso) {
		Progresso.setValue(progresso);
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	public void pausar() {
		cliente.pausar();
	}
	
	public void continuar() {
		cliente.continuar();
	}
	public void cancelar() throws IOException {
		cliente.cancelar();
	}
	public void setRestante(int tempo) {
		if(tempo == 2147483647) {
			restante.setText("Tempo restante: infinito");
		}else {
			restante.setText("Tempo restante: "+ tempo+" Segundos");
		}
	}
	public void terminado() {
		cancelar.setVisible(false);
		pausar.setVisible(false);
		Taxa.setVisible(false);
		RTT.setVisible(false);
		restante.setVisible(false);
	}
	public void upload() {
		cancelar.setVisible(false);
		pausar.setVisible(false);
		this.setTitle("Enviando Arquivo");
	}
	public void setNomeArquivo(String nome) {
		this.parte.setText("Arquivo : "+nome);
	}
	public void trocar() {
		if(pausar.getText().equals("Pausar")) {
			pausar.doClick();
		}
	}
	public void setUpload(ServidorUpload upload) {
		this.upload = upload;
	}
}
