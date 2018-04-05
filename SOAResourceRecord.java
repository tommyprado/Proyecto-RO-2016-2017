package es.uvigo.det.ro.simpledns;

import static es.uvigo.det.ro.simpledns.RRType.SOA;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class SOAResourceRecord extends ResourceRecord {

	private final DomainName dom;
	private final DomainName admin;
	private final int serial;
	private final int refresh;
	private final int retry;
	private final int expire;
	private final int minimum;

	public SOAResourceRecord(DomainName domain, int ttl, DomainName dom, DomainName admin, int serial, int refresh,
			int retry, int expire, int minimum) {

		super(domain, SOA, ttl, SOA.toByteArray());

		this.dom = dom;
		this.admin = admin;
		this.serial = serial;
		this.refresh = refresh;
		this.retry = retry;
		this.expire = expire;
		this.minimum = minimum;
	}

	protected SOAResourceRecord(ResourceRecord decoded, final byte[] message) {
		super(decoded);
		byte[] buffer = getRRData();
		final int length = getRRData().length;
		dom = new DomainName(buffer, message);
		buffer = Arrays.copyOfRange(buffer, dom.getEncodedLength(), length);
		admin = new DomainName(buffer, message);
		buffer = Arrays.copyOfRange(buffer, admin.getEncodedLength(), length);
		serial = Utils.int32fromByteArray(buffer);
		buffer = Arrays.copyOfRange(buffer, 4, length);
		refresh = Utils.int32fromByteArray(buffer);
		buffer = Arrays.copyOfRange(buffer, 4, length);
		retry = Utils.int32fromByteArray(buffer);
		buffer = Arrays.copyOfRange(buffer, 4, length);
		expire = Utils.int32fromByteArray(buffer);
		buffer = Arrays.copyOfRange(buffer, 4, length);
		minimum = Utils.int32fromByteArray(buffer);
		buffer = Arrays.copyOfRange(buffer, 4, length);

	}

	public final DomainName getDom() {
		return dom;
	}

	public final DomainName getAdmin() {
		return admin;
	}

	public final int getSerial() {
		return serial;
	}

	public final int getRefresh() {
		return refresh;
	}

	public final int getRetry() {
		return retry;
	}

	public final int getExpire() {
		return expire;
	}

	public final int getMinimum() {
		return minimum;
	}

	@Override
	public byte[] toByteArray() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
			os.write(super.toByteArray());
			os.write(SOA.toByteArray());
		} catch (IOException ex) {
			Logger.getLogger(SOAResourceRecord.class.getName()).log(Level.SEVERE, null, ex);
			System.exit(-1);
		}

		return os.toByteArray();
	}

	@Override
	public String toString() {
		return this.getRRType() + " " + this.getTTL() + " " + this.getDom() + " " + this.getAdmin() + " "
				+ this.getSerial() + " " + this.getRefresh() + " " + this.getRetry() + " " + this.getExpire() + " "
				+ this.getMinimum();
	}

}