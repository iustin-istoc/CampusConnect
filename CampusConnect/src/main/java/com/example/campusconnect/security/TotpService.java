package com.example.campusconnect.security;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Serviciu TOTP (Time-based One-Time Password) conform RFC 6238.
 * Compatibil cu Google Authenticator / Microsoft Authenticator / Authy.
 *
 *  - cheie secreta de 20 bytes, codificata Base32 (RFC 4648)
 *  - cod pe 6 cifre, HMAC-SHA1, perioada de 30 secunde
 *  - verificare cu fereastra +/- 1 pas (tolereaza mici diferente de ceas)
 */
@Service
public class TotpService {

    private static final String ISSUER = "CampusConnect";
    private static final int DIGITS = 6;
    private static final int PERIOD_SECONDS = 30;
    private static final String BASE32 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";

    private final SecureRandom random = new SecureRandom();

    /** Genereaza o cheie secreta noua (20 bytes) codificata Base32. */
    public String generateSecret() {
        byte[] buffer = new byte[20];
        random.nextBytes(buffer);
        return base32Encode(buffer);
    }

    /** Construieste URI-ul otpauth:// pe care il citeste aplicatia de autentificare. */
    public String getOtpAuthUri(String email, String secret) {
        return "otpauth://totp/" + ISSUER + ":" + email +
                "?secret=" + secret +
                "&issuer=" + ISSUER +
                "&algorithm=SHA1&digits=" + DIGITS + "&period=" + PERIOD_SECONDS;
    }

    /** Genereaza un cod QR (PNG) ca string Base64, pentru a fi afisat in browser. */
    public String generateQrBase64(String email, String secret) {
        try {
            String uri = getOtpAuthUri(email, secret);
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(uri, BarcodeFormat.QR_CODE, 240, 240);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Eroare la generarea codului QR", e);
        }
    }

    /** Verifica daca un cod introdus de utilizator este valid acum (cu toleranta +/- 1 pas). */
    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null) return false;
        code = code.trim();
        if (!code.matches("\\d{" + DIGITS + "}")) return false;

        long timeWindow = System.currentTimeMillis() / 1000L / PERIOD_SECONDS;
        byte[] key = base32Decode(secret);

        for (int offset = -1; offset <= 1; offset++) {
            String candidate = generateCode(key, timeWindow + offset);
            if (constantTimeEquals(candidate, code)) {
                return true;
            }
        }
        return false;
    }

    // ── implementare RFC 6238 / RFC 4226 ────────────────────────────────────

    private String generateCode(byte[] key, long counter) {
        try {
            byte[] data = ByteBuffer.allocate(8).putLong(counter).array();
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);

            int otp = binary % (int) Math.pow(10, DIGITS);
            return String.format("%0" + DIGITS + "d", otp);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la generarea codului TOTP", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    // ── Base32 (RFC 4648) ───────────────────────────────────────────────────

    private String base32Encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int buffer = 0, bitsLeft = 0;
        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                int index = (buffer >> (bitsLeft - 5)) & 0x1F;
                bitsLeft -= 5;
                sb.append(BASE32.charAt(index));
            }
        }
        if (bitsLeft > 0) {
            int index = (buffer << (5 - bitsLeft)) & 0x1F;
            sb.append(BASE32.charAt(index));
        }
        return sb.toString();
    }

    private byte[] base32Decode(String s) {
        s = s.replace("=", "").toUpperCase();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int buffer = 0, bitsLeft = 0;
        for (char c : s.toCharArray()) {
            int val = BASE32.indexOf(c);
            if (val < 0) continue;
            buffer = (buffer << 5) | val;
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                out.write((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }
        return out.toByteArray();
    }
}
