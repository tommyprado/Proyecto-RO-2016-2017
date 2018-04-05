package es.uvigo.det.ro.simpledns;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DNSRequestTCP extends DNSRequest {

	public DNSRequestTCP(Message question, InetAddress ns) {
		super(question, ns);
	}

	public Message send() throws Exception {

		Message mensajeRespuesta = null;

		Socket s = new Socket(getNs(), 53);

		DataInputStream in = new DataInputStream(s.getInputStream());
		DataOutputStream out = new DataOutputStream(s.getOutputStream());

		byte[] longitudConsulta = Utils.int16toByteArray(getQuestion().toByteArray().length);
		out.write(longitudConsulta);
		out.write(getQuestion().toByteArray());
		out.flush();
		byte[] longitudMensajeRespuestaBytes = new byte[2];
		in.readFully(longitudMensajeRespuestaBytes);

		int longitudMensajeRespuesta = Utils.int16fromByteArray(longitudMensajeRespuestaBytes);
		byte[] respuesta_bytes = new byte[longitudMensajeRespuesta];

		in.readFully(respuesta_bytes);
		mensajeRespuesta = new Message(respuesta_bytes);

		in.close();
		out.close();
		s.close();

		return mensajeRespuesta;

	}
}
