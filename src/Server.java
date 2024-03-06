import java.awt.EventQueue;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import Model.Ticket;
import PDFGenerator.DataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.Box;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Component;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Server {
	/**
	 * Esta clase representa la aplicación del servidor para la gestión de tickets.
	 * Maneja las solicitudes de los clientes para comprar, devolver y generar informes en PDF.
	 * @author javit
	 */
	private JTable table;
	private DefaultTableModel tableModel;
	private static Box client_name = Box.createVerticalBox();
	private static Box client_quan = Box.createVerticalBox();
	private static Box matchS = Box.createVerticalBox();
	private static Box ubication = Box.createVerticalBox();
	private static Box client_type_1 = Box.createVerticalBox();

	private static Box client_type_2 = Box.createVerticalBox();
	private final JLabel lblNewLabel_4_2 = new JLabel("Total");
	private final JLabel lblNewLabel_4_1 = new JLabel("Precio");

	private JFrame frame;

	public static Map<String, Integer> getEntradas() {
		return entradas;
	}

	public static void setEntradas(Map<String, Integer> entradas) {
		Server.entradas = entradas;
	}

	private static Map<String, Integer> entradas;
	private final JLabel lblNewLabel_5 = new JLabel("Entradas disponibles");
	private final JLabel lblNewLabel_5_1 = new JLabel("Tipo 1:");
	private final JLabel lblNewLabel_5_2 = new JLabel("Tipo 2:");
	private final JLabel lblNewLabel_5_2_1 = new JLabel("Tipo 3:");
	private static final JLabel left1 = new JLabel("0");
	private static final JLabel left2 = new JLabel("0");
	private static final JLabel left3 = new JLabel("0");
	private final JLabel lblNewLabel_5_3 = new JLabel("Facturacion");
	private final JLabel lblNewLabel_5_1_1 = new JLabel("Tipo 1:");
	private final JLabel lblNewLabel_5_1_1_1 = new JLabel("Tipo 2:");
	private final JLabel lblNewLabel_5_1_1_2 = new JLabel("Tipo 3:");
	private static JLabel total1 = new JLabel("0");
	private static JLabel total2 = new JLabel("0");
	private static JLabel total3 = new JLabel("0");
	private final JLabel lblNewLabel_5_3_1 = new JLabel("Facturacion total");
	private static JLabel totaltotal = new JLabel("0");

	private static ArrayList<Ticket> entradasPDF= new ArrayList<Ticket>();

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server window = new Server();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		entradas = new HashMap<String, Integer>();
		entradas.put("tipo1", 10);
		entradas.put("tipo2", 10);
		entradas.put("tipo3", 10);
	}

	/**
     * Constructor de la clase Server.
     */
	public Server() {
		initialize();
		new Thread(() -> {
			try {
				serverOn();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();

	}
	/**
	 * Inicia el servidor y espera conexiones de clientes.
	 * @throws IOException Si se produce un error al abrir el socket.
	 * @throws ClassNotFoundException ClassNotFoundException Si no se encuentra la clase del objeto
	 */
	private static void serverOn() throws IOException, ClassNotFoundException {
		// duda esto va aqui? no se esta iniciando un servidor nuevo?
		ServerSocket server = new ServerSocket(1234);

		while (true) {
			/*if(entradas.get("tipo1")==0 & entradas.get("tipo2")==0 & entradas.get("tipo3")==0) {
				server.close();
			}*/
			Socket cliente = server.accept();

			solicitudCliente(cliente);
		}

	}
	/**
	 * Recoge la solicitud del cliente y lo desvia a un metodo u otro dependiendo de la accioón del Ticket
	 * @param cliente El socket que representa la conexión del cliente.
	 * @throws IOException IOException Si se produce un error al enviar o recibir datos.
	 * @throws ClassNotFoundException ClassNotFoundException Si no se encuentra la clase del objeto serializado.
	 */
	private static void solicitudCliente(Socket cliente) throws IOException, ClassNotFoundException {
		// creamos la entrada especiaal para objetos que nos llega del cliente
		ObjectInputStream mensajeDelCliente = new ObjectInputStream(cliente.getInputStream());
		// aqui recogemos el objeto ticket y su cantidad
		Ticket solicitudClient = new Ticket();
		solicitudClient = (Ticket) mensajeDelCliente.readObject();
		System.out.println(solicitudClient.toString());

		char action = solicitudClient.getAction();
		
		switch (action) {
		case 'S':// solicitudCompra
			solicitudCompra(mensajeDelCliente, cliente, solicitudClient);
			break;
		case 'D':// devolucion
			devolucion(mensajeDelCliente, cliente, solicitudClient);
			break;
		case 'C':// devolucion
			comprar(mensajeDelCliente, cliente, solicitudClient);
			break;
		}
	}
	/**
	 *Maneja la solicitud de compra de un cliente.
	 * @param mensajeDelCliente El flujo de entrada del cliente.
	 * @param cliente El socket del cliente.
	 * @param solicitudClient La solicitud de compra de tickets.
	 * @throws IOException Si se produce un error al enviar datos.
	 */
	private static void solicitudCompra(ObjectInputStream mensajeDelCliente, Socket cliente, Ticket solicitudClient)
			throws IOException {
		// creamos la salida para contestar al cliente cliente
		OutputStream mensajeACliente = cliente.getOutputStream();
		DataOutputStream salida = new DataOutputStream(mensajeACliente);
		try {

			if (checkTickets(solicitudClient)) {
				// mandamos boolean a ese cliente para que siga con la siguiente pantalla

				salida.writeBoolean(true);
			} else {
				// mandamos false para que le salte un alert
				salida.writeBoolean(false);
			}
			salida.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * Comprueba si hay suficientes tickets disponibles para la compra.
	 * @param compra La solicitud de compra de tickets.
	 * @return True si hay suficientes tickets disponibles, false en caso contrario.
	 */
	private static boolean checkTickets(Ticket compra) {
		
		int left = entradas.get(compra.getTipo());
		int wants = compra.getCantidad();

		if (left >= wants) {
			// restamos
			entradas.replace(compra.getTipo(), left - wants);
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Realiza la compra de tickets para un cliente.
	 * @param mensajeDelCliente El flujo de entrada para recibir mensajes del cliente.
	 * @param cliente El socket del cliente.
	 * @param solicitudClient El objeto Ticket que contiene la información de la solicitud de compra.
	 */
	private static void comprar(ObjectInputStream mensajeDelCliente, Socket cliente, Ticket solicitudClient) {

		JLabel name = new JLabel(solicitudClient.getNombreCliente());
		client_name.add(name);
		client_name.revalidate();
		
		String ubicacionString= solicitudClient.getGate()+"/"+solicitudClient.getRow()+"/"+solicitudClient.getSeat();
		JLabel ubicacion= new JLabel(ubicacionString);
		ubication.add(ubicacion);
		ubication.revalidate();
		
		String match= solicitudClient.getMatch();
		String rivalString= match.substring(match.indexOf("-")+2, match.length());
		JLabel partido = new JLabel(rivalString);
		matchS.add(partido);
		matchS.revalidate();
		
		JLabel cantidad = new JLabel(String.valueOf(solicitudClient.getCantidad()));
		client_quan.add(cantidad);
		client_quan.revalidate();
		JLabel precio = new JLabel(String.valueOf( solicitudClient.getPrecio()));
		client_type_1.add(precio);
		client_type_1.revalidate();
		JLabel total = new JLabel(String.valueOf( solicitudClient.getPrecio()*solicitudClient.getCantidad()));
		client_type_2.add(total);
		client_type_2.revalidate();

		left1.setText(entradas.get("tipo1").toString());
		left1.revalidate();
		left2.setText(entradas.get("tipo2").toString());
		left2.revalidate();
		left3.setText(entradas.get("tipo3").toString());
		left3.revalidate();
		
		actualizarTotal(solicitudClient.getTipo(),Double.parseDouble(total.getText()));
		
		entradasPDF.add(solicitudClient);
	}
	/**
	 * Actualiza el total y refresca la interfaz de facturación y la cantidad total de tickets vendidos de un tipo específico.
	 * @param tipo El tipo de tickets vendidos.
	 * @param parseDouble El valor del total a agregar.
	 */
	private static void actualizarTotal(String tipo, double parseDouble) {
		Double totalFacturacion=Double.parseDouble(totaltotal.getText());
		switch (tipo) {
		case "tipo1": 
			Double totalActualDouble= Double.parseDouble(total1.getText());
			Double nuevoTotal1= totalActualDouble+parseDouble;
			totalFacturacion+=parseDouble;
			total1.setText(String.valueOf(nuevoTotal1));
			break;
		case "tipo2": 
			Double totalActualDouble2= Double.parseDouble(total2.getText());
			Double nuevoTotal2= totalActualDouble2+parseDouble;
			totalFacturacion+=parseDouble;
			total2.setText(String.valueOf(nuevoTotal2));
			break;
		case "tipo3": 
			Double totalActualDouble3= Double.parseDouble(total3.getText());
			Double nuevoTotal3= totalActualDouble3+parseDouble;
			totalFacturacion+=parseDouble;
			total3.setText(String.valueOf(nuevoTotal3));
			break;
		
		}
		
		totaltotal.setText(String.valueOf(totalFacturacion));
		
	}
	/**
	 * Procesa la devolución de tickets y actualiza la cantidad de tickets disponibles en el inventario.
	 * @param mensajeDelCliente El flujo de entrada del mensaje del cliente.
	 * @param cliente El socket del cliente.
	 * @param solicitudClient La solicitud de devolución de tickets.
	 */
	private static void devolucion(ObjectInputStream mensajeDelCliente, Socket cliente, Ticket solicitudClient) {
		String tipoString = solicitudClient.getTipo();
		int cantidadDevolver = solicitudClient.getCantidad();
		int cantidadHay = entradas.get(tipoString);

		entradas.replace(tipoString, cantidadDevolver + cantidadHay);
	
	}
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 814, 496);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		client_name.setBounds(10, 10, 165, 301);
		frame.getContentPane().add(client_name);

		JLabel lblNewLabel = new JLabel("Cliente");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 19));
		client_name.add(lblNewLabel);
		
		ubication.setBounds(185, 10, 152, 301);
		frame.getContentPane().add(ubication);

		JLabel lblNewLabel_1 = new JLabel("Ubicación");
		lblNewLabel_1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		ubication.add(lblNewLabel_1);

		matchS.setBounds(347, 10, 179, 301);
		frame.getContentPane().add(matchS);

		JLabel lblNewLabel_2 = new JLabel("Partido");
		lblNewLabel_2.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 19));
		matchS.add(lblNewLabel_2);

		client_quan.setBounds(536, 10, 83, 301);
		frame.getContentPane().add(client_quan);

		JLabel lblNewLabel_3 = new JLabel("Cantidad");
		lblNewLabel_3.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 19));
		client_quan.add(lblNewLabel_3);
		lblNewLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_5.setAlignmentX(0.5f);
		lblNewLabel_5.setBounds(10, 321, 202, 23);
		
		frame.getContentPane().add(lblNewLabel_5);
		lblNewLabel_5_1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_5_1.setAlignmentX(0.5f);
		lblNewLabel_5_1.setBounds(33, 354, 61, 23);
		
		frame.getContentPane().add(lblNewLabel_5_1);
		lblNewLabel_5_2.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_5_2.setAlignmentX(0.5f);
		lblNewLabel_5_2.setBounds(33, 386, 61, 23);
		
		frame.getContentPane().add(lblNewLabel_5_2);
		lblNewLabel_5_2_1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_5_2_1.setAlignmentX(0.5f);
		lblNewLabel_5_2_1.setBounds(33, 419, 61, 23);
		
		frame.getContentPane().add(lblNewLabel_5_2_1);
		left1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		left1.setAlignmentX(0.5f);
		left1.setBounds(104, 354, 61, 23);
		
		frame.getContentPane().add(left1);
		left2.setFont(new Font("Tahoma", Font.PLAIN, 19));
		left2.setAlignmentX(0.5f);
		left2.setBounds(104, 386, 61, 23);
		
		frame.getContentPane().add(left2);
		left3.setFont(new Font("Tahoma", Font.PLAIN, 19));
		left3.setAlignmentX(0.5f);
		left3.setBounds(104, 419, 61, 23);
		
		frame.getContentPane().add(left3);
		
		left1.setText(entradas.get("tipo1").toString());

		left2.setText(entradas.get("tipo2").toString());

		left3.setText(entradas.get("tipo3").toString());
		lblNewLabel_5_3.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_5_3.setAlignmentX(0.5f);
		lblNewLabel_5_3.setBounds(417, 321, 202, 23);
		
		frame.getContentPane().add(lblNewLabel_5_3);
		lblNewLabel_5_1_1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_5_1_1.setAlignmentX(0.5f);
		lblNewLabel_5_1_1.setBounds(440, 354, 61, 23);
		
		frame.getContentPane().add(lblNewLabel_5_1_1);
		lblNewLabel_5_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_5_1_1_1.setAlignmentX(0.5f);
		lblNewLabel_5_1_1_1.setBounds(440, 386, 61, 23);
		
		frame.getContentPane().add(lblNewLabel_5_1_1_1);
		lblNewLabel_5_1_1_2.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_5_1_1_2.setAlignmentX(0.5f);
		lblNewLabel_5_1_1_2.setBounds(440, 419, 61, 23);
		
		frame.getContentPane().add(lblNewLabel_5_1_1_2);
		total1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		total1.setAlignmentX(0.5f);
		total1.setBounds(511, 354, 61, 23);
		
		frame.getContentPane().add(total1);
		total2.setFont(new Font("Tahoma", Font.PLAIN, 19));
		total2.setAlignmentX(0.5f);
		total2.setBounds(511, 386, 61, 23);
		
		frame.getContentPane().add(total2);
		total3.setFont(new Font("Tahoma", Font.PLAIN, 19));
		total3.setAlignmentX(0.5f);
		total3.setBounds(511, 419, 61, 23);
		
		frame.getContentPane().add(total3);
		client_type_1.setBounds(625, 9, 71, 301);
		
		frame.getContentPane().add(client_type_1);
		lblNewLabel_4_1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_4_1.setAlignmentX(0.5f);
		
		client_type_1.add(lblNewLabel_4_1);
		client_type_2.setBounds(708, 8, 71, 301);
		
		frame.getContentPane().add(client_type_2);
		lblNewLabel_4_2.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_4_2.setAlignmentX(0.5f);
		
		client_type_2.add(lblNewLabel_4_2);
		lblNewLabel_5_3_1.setFont(new Font("Tahoma", Font.PLAIN, 19));
		lblNewLabel_5_3_1.setAlignmentX(0.5f);
		lblNewLabel_5_3_1.setBounds(637, 321, 142, 23);
		
		frame.getContentPane().add(lblNewLabel_5_3_1);
		totaltotal.setHorizontalAlignment(SwingConstants.RIGHT);
		totaltotal.setFont(new Font("Tahoma", Font.PLAIN, 24));
		totaltotal.setAlignmentX(0.5f);
		totaltotal.setBounds(718, 354, 61, 23);
		
		frame.getContentPane().add(totaltotal);
		
		JButton btnNewButton = new JButton("GENERAR PDF");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				resumenPDF();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		btnNewButton.setBounds(625, 402, 154, 43);
		frame.getContentPane().add(btnNewButton);

	}
	/**
	 * Genera un resumen en formato PDF de las transacciones de tickets.
	 */

	protected void resumenPDF() {
		
		try {
			JasperReport myJasper= JasperCompileManager.compileReport("jasper/ticketsResume.jrxml");
			JasperPrint infoJasper= JasperFillManager.fillReport(myJasper, null, new DataSource(entradasPDF));
			JasperViewer.viewReport(infoJasper);
		} catch (JRException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
