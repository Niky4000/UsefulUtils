/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import static ru.kiokle.svetapng.ImageCreateController.s;

/**
 *
 * @author me
 */
public class Keys {

    private static final String privateKeyPath = ImageCreateController.getSaveFolder().getAbsolutePath() + s + "private.key";
    private static final String publicKeyPath = ImageCreateController.getSaveFolder().getAbsolutePath() + s + "public.key";
    private static final String signaturePath = ImageCreateController.getSaveFolder().getAbsolutePath() + s + "signature.key";
    private static final String algorithm = "RSA"; // or RSA, DH, etc.

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(512);
        byte[] privateKey = keyGen.genKeyPair().getPrivate().getEncoded();
        byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
        writeToFile(privateKey, privateKeyPath);
        writeToFile(publicKey, publicKeyPath);
        StringBuffer retString = new StringBuffer();
        retString.append("[");
        for (int i = 0; i < publicKey.length; ++i) {
            retString.append(publicKey[i]);
            retString.append(", ");
        }
        retString = retString.delete(retString.length() - 2, retString.length());
        retString.append("]");
        System.out.println(retString); //e.g. [48, 92, 48, .... , 0, 1]
        return keyGen.genKeyPair();
    }

    private static void writeToFile(byte[] privateKey, String filePath) throws IOException {
        new File(filePath).getParentFile().mkdirs();
        if (new File(filePath).exists()) {
            new File(filePath).delete();
        }
        Files.write(new File(filePath).toPath(), privateKey, StandardOpenOption.CREATE_NEW);
    }

    public static KeyPair generateKeyPair2() throws Exception {
        // Generate a 1024-bit Digital Signature Algorithm (DSA) key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
        keyGen.initialize(1024);
        KeyPair keypair = keyGen.genKeyPair();
        PrivateKey privateKey = keypair.getPrivate();
        PublicKey publicKey = keypair.getPublic();

        byte[] privateKeyBytes = privateKey.getEncoded();
        byte[] publicKeyBytes = publicKey.getEncoded();
        writeToFile(privateKeyBytes, privateKeyPath);
        writeToFile(publicKeyBytes, publicKeyPath);

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey2 = keyFactory.generatePublic(publicKeySpec);

        // The orginal and new keys are the same
        boolean same = privateKey.equals(privateKey2);
        boolean same2 = publicKey.equals(publicKey2);
        return new KeyPair(publicKey, privateKey);
    }

    public static PublicKey loadPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey2 = keyFactory.generatePublic(publicKeySpec);
        return publicKey2;
    }

    public static KeyPair loadKeys4() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
        byte[] publicKeyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey2 = keyFactory.generatePublic(publicKeySpec);
        return new KeyPair(publicKey2, privateKey2);
    }

    public static KeyPair loadKeys() throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
        byte[] publicKeyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey = kf.generatePrivate(keySpecPKCS8);
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(publicKeyBytes);
        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        System.out.println(privateKey);
        System.out.println(publicKey);
        return new KeyPair(publicKey, privateKey);
    }

    public static KeyPair loadKeys2() throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
        byte[] publicKeyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
        KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        return new KeyPair(publicKey, privateKey);
    }

    public static KeyPair loadKeys3() {
        try {
            byte[] privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
            byte[] publicKeyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory generator = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = generator.generatePrivate(privateKeySpec);
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            PublicKey publicKey = generator.generatePublic(publicKeySpec);
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create KeyPair from provided encoded keys", e);
        }
    }

    public static byte[] sign(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signatureBytes = signature.sign();
        return signatureBytes;
    }

    public static boolean verify(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signatureBytes);
    }

    public static String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));
        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return publicSignature.verify(signatureBytes);
    }
}
