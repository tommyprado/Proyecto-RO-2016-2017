/*
 * Copyright (C) 2016 Miguel Rodriguez Perez <miguel@det.uvigo.gal>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.uvigo.det.ro.simpledns;

import java.util.Arrays;

/**
 *
 * @author Miguel Rodriguez Perez <miguel@det.uvigo.gal>
 */
class Utils {

    public static byte[] int16toByteArray(int i) {
        byte[] output = new byte[2];

        output[0] = (byte) ((i & 0xFF00) >> 8);
        output[1] = (byte) (i & 0x00FF);

        return output;
    }

    public static byte[] int32toByteArray(int i) {
        byte[] output = new byte[4];

        output[0] = (byte) ((i & 0xFF000000) >> 24);
        output[1] = (byte) ((i & 0x00FF0000) >> 16);
        output[2] = (byte) ((i & 0x0000FF00) >> 8);
        output[3] = (byte) (i & 0x000000FF);

        return output;
    }

    public static int int16fromByteArray(byte[] val) {
        return (val[0] << 8) + (val[1] & 0xff);
    }

    public static int int32fromByteArray(byte[] val) {
        int value = 0;

        for (int i = 0; i < 4; i++) {
            value = (value << 8) + (val[i] & 0xff);
        }

        return value;
    }

	public static boolean esOctetoValido(String octeto) {

		if (!octeto.matches("\\d{1,3}"))
			return false;

		if (Integer.parseInt(octeto) < 0 || Integer.parseInt(octeto) > 255)
			return false;

		return true;
	}

	public static boolean esIPValida(String ip) {

		String[] octetos = ip.split("\\.", -1);

		return Arrays.stream(octetos).filter(Utils::esOctetoValido) // Cada octeto es v�lido
				.count() == 4; // Son 4 octetos
	}

	public static boolean ArgumentosValidos(String[] argumentos) {
		// Dos argumentos
		if (argumentos.length != 2)
			return false;

		// El primer argumento es -t o -u
		if (!argumentos[0].toLowerCase().equals("-t") && !argumentos[0].toLowerCase().equals("-u"))
			return false;

		// El segundo argumento es una direcci�n IP
		if (!Utils.esIPValida(argumentos[1]))
			return false;

		return true;
	}

	public static void ImprimirInstrucciones() {
		System.out.println("Uso: dnsclient -t|u ip_servidor_raiz");
	}

	public static RRType Str2RRType(String rrtype) {

		switch (rrtype) {
		case "A":
			return RRType.A;
		case "NS":
			return RRType.NS;
		case "CNAME":
			return RRType.CNAME;
		case "SOA":
			return RRType.SOA;
		case "PTR":
			return RRType.PTR;
		case "HINFO":
			return RRType.HINFO;
		case "MX":
			return RRType.MX;
		case "TXT":
			return RRType.TXT;
		case "AAAA":
			return RRType.AAAA;
		case "SRV":
			return RRType.SRV;
		default:
			return null;
		}

	}

}
