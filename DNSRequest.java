package es.uvigo.det.ro.simpledns;

import java.net.InetAddress;

public class DNSRequest {

	private Message question;
	private InetAddress ns;

	public DNSRequest(Message question, InetAddress ns) {
		this.question = question;
		this.ns = ns;
	}

	public Message getQuestion() {
		return question;
	}

	public InetAddress getNs() {
		return ns;
	}

}
