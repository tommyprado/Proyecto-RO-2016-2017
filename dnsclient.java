package es.uvigo.det.ro.simpledns;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class dnsclient {

	/**
     * Caché en memoria implementada con un HashMap
     *
     * La clave es un hash del string formado al concatenar
     * el dominio y el tipo de registro consultado
     * (dominio+"/"+tipo).hashCode()
     *
     * El valor es la respuesta a la consulta DNS
     */
	private static Map<Integer, Message> cache = new HashMap<>();

	/**
	 * Poner esta constante en true si queremos que el
	 * programa nos presente una salida más detallada.
	 */
	private static Boolean VERBOSE = false;

	public static void main(String[] args) throws Exception {

		// Validación de argumentos
		if ( !Utils.ArgumentosValidos(args) ) {
			Utils.ImprimirInstrucciones();
			System.exit(-1);
		}

		final String protocolo = args[0].substring(1);
		final String dnsRoot = args[1];

		Scanner in = new Scanner(System.in);
		String entrada;
		String[] consulta = new String[2];

		// Solicitando entrada de datos
		System.out.println("RRType" + "\t" + "Nombre");

		while( in.hasNextLine() ) {

			// Obteniendo datos de la consulta
			entrada = in.nextLine();
			consulta = entrada.split(" ");
			String dominio = consulta[1];
			String tipo = consulta[0].toUpperCase();

			// Obteniendo respuesta DNS
			respuestaDNS(dominio, tipo, InetAddress.getByName(dnsRoot), protocolo);

			// Solicitando nueva entrada de datos
			System.out.println("\n\n" + "RRType" + "\t" + "Nombre");
		}

		in.close();
		System.exit(-1);
	}

	public static void respuestaDNS(String dominio, String tipo, InetAddress dnsRoot, String protocolo) throws Exception {

		// Inicialización del mensaje que contiene la consulta
		final RRType rrtype = Utils.Str2RRType(tipo);
		final Message question = new Message(dominio, rrtype, false);

		// Inicialización del mensaje que contendrá la respuesta
		Message answer = null;

		// Inicialización del NS asignándole como valor el dns raíz
		InetAddress ns = dnsRoot;
		
		// hash único de la consulta para localización en caché
		int hash = (dominio+"/"+tipo).hashCode();

		do {
			// Buscar en caché.
			// Si no se encuentra, realizar consulta UDP o TCP
			// dependiendo del valor del parámetro protocolo
			if (cache.containsKey(hash)) {
				System.out.println("\n" + "Q cache cache " + tipo + " " + dominio);
				answer = cache.get(hash);
			} else if (protocolo.toLowerCase().equals("u")) {
				System.out.println("\n" + "Q UDP " + ns.getHostAddress() + " " + tipo + " " + dominio);
				answer = new DNSRequestUDP(question, ns).send();
			} else if (protocolo.toLowerCase().equals("t")) {
				System.out.println("\n" + "Q TCP " + ns.getHostAddress() + " " + tipo + " " + dominio);
				answer = new DNSRequestTCP(question, ns).send();
			}

			// Imprimir la sección de respuestas
			if (answer.getAnswers().size() > 0) {

				if (VERBOSE) {
					System.out.println("\n;;ANSWER SECTION");
				}

				for ( ResourceRecord rr : answer.getAnswers() ) {
					if(rr.getRRType().toString().trim().toUpperCase().equals(tipo.trim().toUpperCase())){
					System.out.println("A " + ns.getHostAddress() + " " + rr);
				}
					}

				// Guardar la consulta en caché 
				if (!cache.containsKey(hash)) {
					cache.put(hash, answer);
				}
			}

			// Imprimir la sección autoritativa
			if (answer.getNameServers().size() > 0) {
				if (VERBOSE) {
					System.out.println("\n;;AUTHORITY SECTION");
					for ( ResourceRecord rr : answer.getNameServers() ) {
						System.out.println("A " + ns.getHostAddress() + " " + rr);
					}
				}
				else if (answer.getAnswers().size() == 0) {
					System.out.println("A " + ns.getHostAddress() + " " + answer.getNameServers().get(0));
				}
			}

			// Imprimir la sección adicional
			if(answer.getAnswers().isEmpty()){
				if (answer.getAdditonalRecords().size() > 0) {
				if (VERBOSE) {
					System.out.println("\n;;ADDITIONAL SECTION");
					for ( ResourceRecord rr : answer.getAdditonalRecords() ) {
						System.out.println("A " + ns.getHostAddress() + " " + rr.getDomain() + " " + rr);
					}
				}
				else if(!answer.getAdditonalRecords().isEmpty()){
					System.out.println("A "+ns.getHostAddress()+ " " +answer.getAdditonalRecords().get(0));
				}
			}
				}

			// Si no hay respuesta ni nuevo NS al que consultar
			if (answer.getAnswers().size() == 0 && answer.getNameServers().size() == 0) {
				System.out.println("Sin sección respuesta y sin NS server en sección autoritativa");
				break;
			}

			// No hay respuesta pero hay NS autoritativo al que consultar
			if (answer.getAnswers().size() == 0 && answer.getNameServers().size() != 0 && answer.getNameServers().get(0).getRRType().toString().trim().toUpperCase()!="SOA") {
				ns = getNextNs(answer);
			}
			if (answer.getAnswers().size() == 0 && answer.getNameServers().size() != 0&& answer.getNameServers().get(0).getRRType().toString().trim().toUpperCase()=="SOA") {
				System.out.println("El Authoritative Nameserve es tipo SOA");
				break;
			}
			if(answer.getAnswers().size()==1 && answer.getAnswers().get(0).getRRType().toString().toUpperCase()=="CNAME" && !answer.getAnswers().get(0).getRRType().toString().trim().toUpperCase().equals(tipo.trim().toUpperCase()) ){
			
				break;
			}
		} while (( answer.getAnswers().size() == 0 || ( answer.getAnswers().size() == 0 && answer.getNameServers().size() == 0 )) );
		if(answer.getAnswers().size()==1 && answer.getAnswers().get(0).getRRType().toString().toUpperCase()=="CNAME" && !answer.getAnswers().get(0).getRRType().toString().trim().toUpperCase().equals(tipo.trim().toUpperCase()) ){
			
			CNAMEResourceRecord b = (CNAMEResourceRecord) answer.getAnswers().get(0);
    		  dominio = b.getCNAME().toString();
    		  respuestaDNS(dominio,tipo,dnsRoot,protocolo);
			
		}
	}

	public static InetAddress getNextNs(Message answer) throws UnknownHostException {
		// Primer NS autoritativo
		
	
			NSResourceRecord nameServer = (NSResourceRecord) answer.getNameServers().get(0);
			return InetAddress.getByName(nameServer.getNS().toString());
		
	}

}
