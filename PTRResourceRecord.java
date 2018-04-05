package es.uvigo.det.ro.simpledns;

import static es.uvigo.det.ro.simpledns.RRType.PTR;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PTRResourceRecord extends ResourceRecord {
	private final DomainName rdns;

	public PTRResourceRecord(DomainName domain, int ttl, DomainName rdns) {
		super(domain, PTR, ttl, RRDataBytes(rdns));
		this.rdns = rdns;
	}

	protected PTRResourceRecord(ResourceRecord decoded, final byte[] message) {
		super(decoded);
		//rdns = new DomainName(Arrays.copyOfRange(getRRData(), 2, getRRData().length), message);
		rdns = new DomainName(getRRData(), message);
	}

	public final DomainName getPTRrdns() {
		return rdns;
	}

	private static byte[] RRDataBytes(DomainName rdns) {
		ByteArrayOutputStream RRDATA = new ByteArrayOutputStream();
		try {
			RRDATA.write(rdns.toByteArray());
		} catch (IOException e) {
			Logger.getLogger(PTRResourceRecord.class.getName()).log(Level.SEVERE, null, e);
			System.exit(-1);
		}
		return RRDATA.toByteArray();
	}

	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			os.write(super.toByteArray());
			os.write(rdns.toByteArray());
		} catch (IOException ex) {
			Logger.getLogger(PTRResourceRecord.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(-1);
		}
		return os.toByteArray();
	}

	@Override
	public String toString() {
    	return this.getRRType() + " " + this.getTTL() + " " + this.getPTRrdns();
	}

}