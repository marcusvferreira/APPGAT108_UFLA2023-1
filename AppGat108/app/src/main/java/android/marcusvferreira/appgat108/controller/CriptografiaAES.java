package android.marcusvferreira.appgat108.controller;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Implementação em Java da criptografia AES (Advanced Encryption Standard), um algoritmo amplamente utilizado para a criptografia de dados.
 * Sobre o AES: https://cryptoid.com.br/criptografia/aes-padrao-de-criptografia-avancado-o-que-e-e-como-funciona/
 */
public class CriptografiaAES {
    private static final String AES_MODE = "AES/CBC/PKCS5Padding";
    private static final String CHARSET_NAME = StandardCharsets.UTF_8.name();

    /**
     * Realiza a criptografia dos dados usando o algoritmo AES.
     *
     * @param dados os dados a serem criptografados
     * @param chave a chave de criptografia
     * @param iv    o vetor de inicialização
     * @return os dados criptografados como uma string em Base64
     * @throws Exception se ocorrer algum erro durante a criptografia
     */
    public static String criptografar(String dados, String chave, String iv) throws Exception {
        byte[] bytesCriptografados;
        SecretKeySpec chaveSecreta = new SecretKeySpec(chave.getBytes(CHARSET_NAME), "AES");
        IvParameterSpec parametroIV = new IvParameterSpec(iv.getBytes(CHARSET_NAME));
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, chaveSecreta, parametroIV);
        bytesCriptografados = cipher.doFinal(dados.getBytes(CHARSET_NAME));
        return Base64.getEncoder().encodeToString(bytesCriptografados);
    }

    /**
     * Realiza a descriptografia dos dados criptografados usando o algoritmo AES.
     *
     * @param dadosCriptografados os dados criptografados em formato Base64
     * @param chave               a chave de criptografia
     * @param iv                  o vetor de inicialização
     * @return os dados descriptografados como uma string
     * @throws Exception se ocorrer algum erro durante a descriptografia
     */
    public static String descriptografar(String dadosCriptografados, String chave, String iv) throws Exception {
        byte[] bytesDescriptografados;
        SecretKeySpec chaveSecreta = new SecretKeySpec(chave.getBytes(CHARSET_NAME), "AES");
        IvParameterSpec parametroIV = new IvParameterSpec(iv.getBytes(CHARSET_NAME));
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.DECRYPT_MODE, chaveSecreta, parametroIV);
        bytesDescriptografados = cipher.doFinal(Base64.getDecoder().decode(dadosCriptografados));
        return new String(bytesDescriptografados, CHARSET_NAME);
    }
}

