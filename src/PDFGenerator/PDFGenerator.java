package PDFGenerator;

import java.util.HashMap;
import java.util.Map;

import Model.Ticket;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class PDFGenerator {
	
	private static Ticket myticket;
	/**
	 * Método principal que se encarga de generar el archivo PDF con los datos del ticket.
	 * 
	 * @param args Los argumentos de línea de comandos (no se utilizan en este caso).
	 */
	public static void main(String[] args) {
		Map<String, Object> mapeoData= new HashMap<String, Object>();
		mapeoData.put("gate", myticket.getGate());
		mapeoData.put("row", myticket.getRow());
		mapeoData.put("seat", myticket.getSeat());
		mapeoData.put("ticketType", myticket.getNombreCliente().toUpperCase());
		mapeoData.put("match", myticket.getMatch());
		
		try {
			JasperReport myJasper= JasperCompileManager.compileReport("jasper/entradaPDF.jrxml");
			JasperPrint infoJasper= JasperFillManager.fillReport(myJasper, mapeoData, new JREmptyDataSource());
			JasperViewer.viewReport(infoJasper);
		} catch (JRException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	/**
	 * Constructor de la clase PDFGenerator.
	 * 
	 * @param myticket El ticket del que se generarán los datos del archivo PDF.
	 */
	public PDFGenerator(Ticket myticket) {
		super();
		this.myticket = myticket;
	}
	
}