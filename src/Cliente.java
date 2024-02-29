
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import Model.Ticket;
import Model.TicketType;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.Image;

import javax.swing.JPanel;

public class Cliente {

	private static ImageIcon icon1 = new ImageIcon("././img/palmas.png");
	private static Image resizedImage1 = icon1.getImage().getScaledInstance(90, 160, Image.SCALE_SMOOTH);
	private static ImageIcon resizedIcon1 = new ImageIcon(resizedImage1);

	private static ImageIcon icon2 = new ImageIcon("././img/inter.png");
	private static Image resizedImage2 = icon2.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
	private static ImageIcon resizedIcon2 = new ImageIcon(resizedImage2);

	private static ImageIcon icon3 = new ImageIcon("././img/barsa.png");
	private static Image resizedImage3 = icon3.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
	private static ImageIcon resizedIcon3 = new ImageIcon(resizedImage3);

	private JFrame frame;
	private static JButton tipo1 = new JButton(resizedIcon1);
	private static JButton tipo2 = new JButton(resizedIcon2);
	private static JButton tipo3 = new JButton(resizedIcon3);
	private static ButtonGroup tickets = new ButtonGroup();

	private static Socket cliente;

	private static JLabel quantity = new JLabel("0");
	JButton quantityMinus = new JButton("-");
	JButton quantityMas = new JButton("+");

	private static JPanel view2 = new JPanel();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Cliente window = new Cliente();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		tickets.add(tipo1);
		tickets.add(tipo2);
		tickets.add(tipo3);
	}

	public Cliente() throws UnknownHostException, IOException {
		initialize();

	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 663, 345);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// view 1

		JPanel view1 = new JPanel();
		view1.setBounds(0, 0, 649, 308);
		frame.getContentPane().add(view1);
		view1.setLayout(null);

		quantity.setHorizontalAlignment(SwingConstants.CENTER);
		quantity.setFont(new Font("Tahoma", Font.PLAIN, 16));
		quantity.setBounds(314, 246, 9, 20);

		view1.add(quantity);

		quantityMinus.setEnabled(false);
		quantityMinus.setBounds(244, 248, 47, 21);

		view1.add(quantityMinus);

		quantityMas.setBounds(355, 248, 47, 21);

		view1.add(quantityMas);
		tipo1.setEnabled(false);

		/*
		 * JLabel img = new JLabel(resizedIcon1); im1.setBounds(427, 10, 178, 257);
		 * frame.getContentPane().add(im1);
		 */

		///////////////////////////////

		tipo1.setBounds(44, 27, 160, 195);
		tipo1.setName("tipo1");

		view1.add(tipo1);
		tipo1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					selectedTicket(tipo1);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		tipo2.setEnabled(false);
		tipo2.setBounds(242, 27, 160, 195);
		tipo2.setName("tipo2");
		view1.add(tipo2);
		tipo2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					selectedTicket(tipo2);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		tipo3.setEnabled(false);
		tipo3.setBounds(442, 27, 160, 195);
		tipo3.setName("tipo3");
		view1.add(tipo3);
		tipo3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					selectedTicket(tipo3);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		quantityMas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int cantidad = Integer.parseInt(quantity.getText());
				if (cantidad <= 0) {
					tipo1.setEnabled(true);
					tipo2.setEnabled(true);
					tipo3.setEnabled(true);
					quantityMinus.setEnabled(true);
				}
				cantidad++;
				quantity.setText(String.valueOf(cantidad));

			}
		});
		quantityMinus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int cantidad = Integer.parseInt(quantity.getText());

				cantidad--;
				if (cantidad <= 0) {
					tipo1.setEnabled(false);
					tipo2.setEnabled(false);
					tipo3.setEnabled(false);
					quantityMinus.setEnabled(false);
				}
				quantity.setText(String.valueOf(cantidad));
			}
		});

	}

	private void selectedTicket(JButton selectedBtn) throws UnknownHostException, IOException {
		cliente = new Socket("localhost", 1234);
		// recoger los datos del boton y la cantidad
		int cantidad = Integer.parseInt(quantity.getText());
		String tipo = selectedBtn.getName();
		char action = 'S';

		String match = "";
		if (tipo == "tipo1") {
			match = "ATLETICO MADRID - LAS PALMAS FC";
		} else if (tipo == "tipo2") {
			match = "ATLETICO MADRID - INTER DE MILAN";
		} else if(tipo == "tipo3"){
			match = "ATLETICO MADRID - FC BARCELONA";
		}

		Ticket ticket = new Ticket(tipo, cantidad, action, match);
		ticket.setPrecio();
		// preguntamos al server si hay entradas
		try {
			solicitudServer(ticket);

			if (hayTickets()) {
				// enviarselo a la otra vista

				ClienteSocket vista2 = new ClienteSocket(selectedBtn, ticket, cliente);
				vista2.main(null);
				frame.dispose();
			} else {
				JOptionPane.showMessageDialog(frame, "No quedan " + String.valueOf(cantidad) + " entradas ese tipo");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean hayTickets() throws IOException {

		DataInputStream entradaCliente = new DataInputStream(cliente.getInputStream());

		return entradaCliente.readBoolean();

	}

	private void solicitudServer(Ticket ticket) throws IOException {

		System.out.println(ticket.toString());
		ObjectOutputStream solicitud = new ObjectOutputStream(cliente.getOutputStream());
		solicitud.writeObject(ticket);
		solicitud.flush();

	}
}
