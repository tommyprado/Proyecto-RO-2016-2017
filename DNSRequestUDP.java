package es.uvigo.det.ro.simpledns;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DNSRequestUDP extends DNSRequest {

	public DNSRequestUDP(Message question, InetAddress ns) {
		super(question, ns);
	}

	public Message send() throws Exception {

		Message mensajeRespuesta = null;

		DatagramSocket s = new DatagramSocket();
		byte[] message = getQuestion().toByteArray();
		s.send(new DatagramPacket(message, message.length, getNs(), 53));

		byte[] respuesta_bytes = new byte[1500];
		DatagramPacket respuesta = new DatagramPacket(respuesta_bytes, respuesta_bytes.length);
		s.receive(respuesta);
		s.close();

		try {
			mensajeRespuesta = new Message(respuesta_bytes);
		} catch (Exception e) {
			//e.printStackTrace();
			if (e.getMessage() == "We do not know what to do with truncated responses") {
				mensajeRespuesta = new DNSRequestTCP(getQuestion(), getNs()).send();
			}
		}

		return mensajeRespuesta;
	}

}
