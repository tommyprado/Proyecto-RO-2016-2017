package es.uvigo.det.ro.simpledns;

import static es.uvigo.det.ro.simpledns.RRType.SRV;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SRVResourceRecord extends ResourceRecord {
	private final int prioridad;
	private final int peso;
	private final int puerto;
	private final DomainName destino;

	public SRVResourceRecord(DomainName domain, int ttl, int prioridad, int peso, int puerto, DomainName destino) {
		super(domain, SRV, ttl, RRDataBytes(prioridad, peso, puerto, destino));
		this.prioridad = prioridad;
		this.peso = peso;
		this.puerto = puerto;
		this.destino = destino;
	}

	protected SRVResourceRecord(ResourceRecord decoded, final byte[] message) {
		super(decoded);
		prioridad = Utils.int16fromByteArray(getRRData());
		peso = Utils.int16fromByteArray(Arrays.copyOfRange(getRRData(), 2, getRRData().length));
		puerto = Utils.int16fromByteArray(Arrays.copyOfRange(getRRData(), 4, getRRData().length));
		destino = new DomainName(Arrays.copyOfRange(getRRData(), 6, getRRData().length), message);
	}

	public final DomainName getSRVdestino() {
		return destino;
	}

	public final int getSRVprioridad() {
		return prioridad;
	}

	public final int getSRVpeso() {
		return peso;
	}

	public final int getSRVpuerto() {
		return puerto;
	}

	private static byte[] RRDataBytes(int prioridad, int peso, int puerto, DomainName destino) {
		ByteArrayOutputStream RRDATA = new ByteArrayOutputStream();
		try {
			RRDATA.write(Utils.int16toByteArray(prioridad));
			RRDATA.write(Utils.int16toByteArray(peso));
			RRDATA.write(Utils.int16toByteArray(puerto));
			RRDATA.write(destino.toByteArray());
		} catch (IOException e) {
			Logger.getLogger(SRVResourceRecord.class.getName()).log(Level.SEVERE, null, e);
			System.exit(-1);
		}
		return RRDATA.toByteArray();
	}

	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(super.toByteArray());
			os.write(Utils.int16toByteArray(prioridad));
			os.write(Utils.int16toByteArray(peso));
			os.write(Utils.int16toByteArray(puerto));
			os.write(destino.toByteArray());
		} catch (IOException ex) {
			Logger.getLogger(SRVResourceRecord.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(-1);
		}
		return os.toByteArray();
	}

	@Override
	public String toString() {
    	return this.getRRType() + " " + this.getTTL() + " " + this.getSRVprioridad() + " " + this.getSRVpeso() + " " + this.getSRVpuerto() + " " + this.getSRVdestino();
	}

}