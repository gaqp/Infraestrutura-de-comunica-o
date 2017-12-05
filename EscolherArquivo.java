import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class EscolherArquivo extends JDialog {
	private Janela1 janela;
	public static void main(String[] args) {
		try {
			EscolherArquivo dialog = new EscolherArquivo();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public EscolherArquivo() {
		setAlwaysOnTop(true);
		setUndecorated(true);
		setVisible(true);
		setEnabled(true);
		setBounds(100, 100, 661, 447);
		{
			JFileChooser escolherArquivo = new JFileChooser();
			escolherArquivo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					fechar();
				}
			});
			getContentPane().add(escolherArquivo, BorderLayout.CENTER);
		}
	}
	public void setJanela(Janela1 janela) {
		this.janela = janela;
	}
	public void fechar() {
		this.setEnabled(false);
		this.setVisible(false);
	}

}
