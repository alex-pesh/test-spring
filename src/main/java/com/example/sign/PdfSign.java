package com.example.sign;

public class PdfSign {

	public static void main(String[] args) {

	}


	public static void signPdf() {

	}


	/**
	 * https://habr.com/ru/companies/alfa/articles/337668/
	 *
	 * @param aliases    имена контейнеров с ключами ЭП
	 * @param data       массив байтов с документом PDF
	 * @param pdfVersion номер версии формата PDF
	 */
/*

	public static byte[] signPdfItext(String[] aliases, byte[] data, char pdfVersion) throws SignatureException {
		ByteArrayOutputStream bais = new ByteArrayOutputStream();
		Map<X509Certificate, PrivateKey> currSignAttrMap = new HashMap<X509Certificate, PrivateKey>();
		for (String alias : aliases) {
			X509Certificate certificate = (X509Certificate) signAttributesMap1.get(alias)[0];
			PrivateKey privateKey = (PrivateKey) signAttributesMap1.get(alias)[1];

			currSignAttrMap.put(certificate, privateKey);
			if (certificate == null) {
				throw new SignatureException("Certificate not found");
			}

			if (privateKey == null) {
				throw new SignatureException("Private key not found");
			}
		}
		try {
			FileInputStream fis = new FileInputStream(new File(FILE_PATH));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int n = 0;
			while ((n = fis.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, n);
			}
			fis.close();
			byte[] im = baos.toByteArray();

			X509Certificate innerCA = obtainCertFromTrustStoreJKS(false, INNER_CA);
			PdfStamper stp = null;
			PdfReader reader = null;
			int pageNumber = 1;
			for (Map.Entry<X509Certificate, PrivateKey> entry : currSignAttrMap.entrySet()) {
				if (bais.toByteArray().length == 0) {
					reader = new PdfReader(data);
				} else {
					reader = new PdfReader(bais.toByteArray());
					bais = new ByteArrayOutputStream();
				}
				stp = PdfStamper.createSignature(reader, bais, pdfVersion); //'\0'
				Certificate[] certPath = new Certificate[]{entry.getKey(), innerCA};
				PdfSignatureAppearance sap = stp.getSignatureAppearance();
				sap.setProvider("JCSP"); //JCP
				sap.setCrypto(entry.getValue(), certPath, null, PdfSignatureAppearance.CRYPTOPRO_SIGNED);
				Image image = Image.getInstance(im);
				sap.setImage(image);
				sap.setVisibleSignature(new Rectangle(150, 150), pageNumber, null);
				pageNumber++;
				stp.close();
				bais.close();
				reader.close();
			}

			return bais.toByteArray();

		} catch (RuntimeException | IOException | DocumentException | CertificateEncodingException e) {
			throw new SignatureException("Failed to sign", e);
		}
	}
*/




}
