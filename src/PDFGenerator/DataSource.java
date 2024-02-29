package PDFGenerator;

import java.util.ArrayList;

import Model.Ticket;
import groovy.lang.Newify;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class DataSource implements JRDataSource {

	ArrayList<Ticket> tickets = new ArrayList<Ticket>();
	private int index;

	public DataSource(ArrayList<Ticket> tickets) {
		super();
		index=-1;
		this.tickets = tickets;
	}

	@Override
	public Object getFieldValue(JRField field) throws JRException {

		Object campo = new Object();
		switch (field.getName()) {
		case "Cliente": {
			campo= tickets.get(index).getNombreCliente();
			break;
		}
		case "Ubicacion": {
			campo= tickets.get(index).getGate()+"/"+tickets.get(index).getRow()+"/"+tickets.get(index).getSeat();
			break;
		}
		case "Partido": {
			String match= tickets.get(index).getMatch();
			String rivalString= match.substring(match.indexOf("-")+2, match.length());
			campo = rivalString;
			break;
		}
		case "Cantidad": {
			campo = tickets.get(index).getCantidad();
			break;
		}
		case "Precio": {
			campo= tickets.get(index).getPrecio();
			break;
		}
		case "Total": {
			campo= tickets.get(index).getPrecio()* tickets.get(index).getCantidad();
			break;
		}
		}

		return campo;
	}

	@Override
	public boolean next() throws JRException {
		index++;
		return (index<tickets.size());
	}

}
