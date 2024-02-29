import java.awt.EventQueue;
import java.awt.Font;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import Model.Ticket;
import PDFGenerator.PDFGenerator;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JSpinner;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class ClienteSocket {

	private static JFrame frame;
	private JTextField nameField;
	private JTextField firstNameField;
	private JTextField emailField;
	JLabel quantityTotal = new JLabel("0");
	private static JTextField countDown;
	private static JButton buttonRecibed;

	private static Ticket ticketReservado;
	private static Socket clienteSocket;
	private static int cantidadTotal;
	private static Thread timerThread;
	
	
	private static JComboBox gateCombo = new JComboBox();
	private static JComboBox rowCombo = new JComboBox();
	private static JComboBox seatCombo = new JComboBox();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClienteSocket window = new ClienteSocket(buttonRecibed, ticketReservado, clienteSocket);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		timerThread = new Thread(() -> {
			int seconds = 30;
			int minutes = 0;
			int totalTime = minutes * 60 + seconds;
			

			while (totalTime > 0 & ticketReservado.getAction()!='C') {
				try {
				
					Thread.sleep(1000);
					seconds--;
					totalTime--;
					if (seconds == 0) {
						minutes--;
						seconds = 0;
					}
					String printSec = (seconds < 10) ? "0" + seconds : String.valueOf(seconds);
					countDown.setText("0" + minutes + ":" + printSec);
					System.out.println(totalTime);
					if (totalTime == 0) {
						// cerrar la ventana
						frame.dispose();
						// devolver las entradas
						returnTickets();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		timerThread.start();
	}

	public ClienteSocket(JButton botonPulsado, Ticket ticket, Socket cliente) {
		initialize();
		buttonRecibed = botonPulsado;
		ticketReservado = ticket;
		clienteSocket = cliente;
		cantidadTotal = ticket.getCantidad();
		frame.getContentPane().add(buttonRecibed);
		
		
		gateCombo.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5"}));
		gateCombo.setBounds(59, 249, 66, 21);
		frame.getContentPane().add(gateCombo);
		
	
		rowCombo.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5"}));
		rowCombo.setBounds(185, 248, 66, 21);
		frame.getContentPane().add(rowCombo);
		
		
		seatCombo.setModel(new DefaultComboBoxModel(new String[] {"1", "2", "3", "4", "5"}));
		seatCombo.setBounds(316, 248, 66, 21);
		frame.getContentPane().add(seatCombo);
		
		JLabel lblNewLabel = new JLabel("Gate");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel.setBounds(59, 209, 65, 22);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Row");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel_1.setBounds(185, 209, 65, 22);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Seat");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel_2.setBounds(316, 209, 65, 22);
		frame.getContentPane().add(lblNewLabel_2);

		buttonRecibed.setBounds(494, 77, 108, 133);
	}

	private void initialize() {
		// Segunda vista
		frame = new JFrame();
		frame.setBounds(100, 100, 663, 345);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel name = new JLabel("Name");
		name.setBounds(57, 34, 106, 13);
		frame.getContentPane().add(name);

		nameField = new JTextField();
		nameField.setBounds(57, 58, 229, 19);
		frame.getContentPane().add(nameField);
		nameField.setColumns(10);

		JLabel lblFirstName = new JLabel("First name");
		lblFirstName.setBounds(57, 87, 106, 13);
		frame.getContentPane().add(lblFirstName);

		firstNameField = new JTextField();
		firstNameField.setColumns(10);
		firstNameField.setBounds(57, 111, 229, 19);
		frame.getContentPane().add(firstNameField);

		JLabel lblEmail = new JLabel("Email");
		lblEmail.setBounds(57, 140, 106, 13);
		frame.getContentPane().add(lblEmail);

		emailField = new JTextField();
		emailField.setColumns(10);
		emailField.setBounds(57, 164, 229, 19);
		frame.getContentPane().add(emailField);

		countDown = new JTextField();
		countDown.setHorizontalAlignment(SwingConstants.CENTER);
		countDown.setEditable(false);
		countDown.setFont(new Font("Tahoma", Font.PLAIN, 16));
		countDown.setBounds(485, 10, 124, 19);
		frame.getContentPane().add(countDown);
		countDown.setColumns(10);

		quantityTotal.setHorizontalAlignment(SwingConstants.CENTER);
		quantityTotal.setFont(new Font("Tahoma", Font.PLAIN, 19));
		quantityTotal.setBounds(531, 221, 45, 30);
		quantityTotal.setText(String.valueOf(cantidadTotal));
		frame.getContentPane().add(quantityTotal);

		JButton button = new JButton("FINALIZAR COMPRA");
		button.setBounds(485, 262, 124, 32);
		frame.getContentPane().add(button);

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int confirm = JOptionPane.showConfirmDialog(frame,
						"Desea confirmar la compra de " + cantidadTotal + " tickets de " + ticketReservado.getTipo());
				if (confirm == 0) {
					try {
						comprarTickets();
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

	}

	private static void returnTickets() throws IOException {
		// TODO invocar el metodo cuando se sale de la pantalla
		// abrimos un canal de salida, le mandamos un objeto ticket cambiando el
		// booleano
		ticketReservado.setAction('D');
		Socket cliente = new Socket("localhost", 1234);
		ObjectOutputStream devolucion = new ObjectOutputStream(cliente.getOutputStream());
		devolucion.writeObject(ticketReservado);
		devolucion.flush();
		cliente.close();
	}

	protected void comprarTickets() throws UnknownHostException, IOException {
		ticketReservado.setSeat(Integer.parseInt( seatCombo.getSelectedItem().toString()));
		ticketReservado.setRow(Integer.parseInt( rowCombo.getSelectedItem().toString()));
		ticketReservado.setGate(Integer.parseInt( gateCombo.getSelectedItem().toString()));
		ticketReservado.setNombreCliente(nameField.getText()+" "+firstNameField.getText());
		ticketReservado.setEmailCliente(emailField.getText());
		ticketReservado.setAction('C');
		
		PDFGenerator pdfGenerator= new PDFGenerator(ticketReservado);
		pdfGenerator.main(null);
		Socket cliente = new Socket("localhost", 1234);
		ObjectOutputStream devolucion = new ObjectOutputStream(cliente.getOutputStream());
		devolucion.writeObject(ticketReservado);
		devolucion.flush();
		cliente.close();
		frame.dispose();
	}
}
