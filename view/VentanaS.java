
package view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import MODEL.Servidor;

public class VentanaS extends JFrame {
	private static final long serialVersionUID = 1L;
	private final String DEFAULT_PORT = "10101";

	public VentanaS() {
		initComponents();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String puerto = getPuerto();
		new Servidor(puerto, this);
	}

	private void initComponents() {
		Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/database.png"));
		setIconImage(image);
		jScrollPane1 = new javax.swing.JScrollPane();
		txtClientes = new javax.swing.JTextArea();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Servidor");

		getContentPane().setForeground(Color.green);

		jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Log del Servidor"));

		txtClientes.setEditable(false);
		txtClientes.setColumns(20);
		txtClientes.setRows(5);
		jScrollPane1.setViewportView(txtClientes);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap()
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
						.addContainerGap()));

		pack();
	}

	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea txtClientes;

	public void agregarLog(String texto) {
		txtClientes.append(texto);
	}

	private String getPuerto() {
		String p = DEFAULT_PORT;
		UIManager.put("OptionPane.foreground", Color.green);

		JTextField puerto = new JTextField(20);
		puerto.setText(p);
		JPanel myPanel = new JPanel();
		myPanel.setLayout(new GridLayout(2, 1));
		JLabel jpuerto = new JLabel("Puerto de la conexión:");
		jpuerto.setForeground(Color.green);
		myPanel.add(jpuerto);
		myPanel.add(puerto);
		int result = JOptionPane.showConfirmDialog(null, myPanel, "Configuraciones de la comunicación",
				JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			p = puerto.getText();
		} else {
			System.exit(0);
		}
		return p;
	}

	public void addServidorIniciado() {
		txtClientes.setText("Inicializando el servidor... [Ok].");
	}
}
