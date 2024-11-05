package living.word.livingword;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import java.security.Key;

public class KeyGenerator {
    public static void main(String[] args) {
        // Genera una clave para HS512
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // Codifica la clave en Base64
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("Clave secreta (Base64): " + base64Key);
    }
}
