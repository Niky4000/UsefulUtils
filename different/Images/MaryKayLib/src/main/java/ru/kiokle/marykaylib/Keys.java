/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.marykaylib;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
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
import org.apache.commons.io.IOUtils;
import static ru.kiokle.marykaylib.CommonUtil.s;

/**
 *
 * @author me
 */
public class Keys {

    private final String PRIVATE_KEY = "private.key";
    private final String PUBLIC_KEY = "public.key";
    private final String SIGNATURE = "signature.key";
    private final File saveFolder;
    private final String privateKeyPath;
    private final String publicKeyPath;
    private final String signaturePath;
    private final String algorithm = "RSA"; // or RSA, DH, etc.

    public Keys(File saveFolder) {
        this.saveFolder = saveFolder;
        this.privateKeyPath = saveFolder.getAbsolutePath() + s + PRIVATE_KEY;
        this.publicKeyPath = saveFolder.getAbsolutePath() + s + PUBLIC_KEY;
        this.signaturePath = saveFolder.getAbsolutePath() + s + SIGNATURE;
    }

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException, IOException {
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

    private void writeToFile(byte[] privateKey, String filePath) throws IOException {
        new File(filePath).getParentFile().mkdirs();
        if (new File(filePath).exists()) {
            new File(filePath).delete();
        }
        Files.write(new File(filePath).toPath(), privateKey, StandardOpenOption.CREATE_NEW);
    }

    public KeyPair generateKeyPair2() throws Exception {
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

    public PublicKey loadPublicKey(Class class_) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = getKeyBytes(class_, PUBLIC_KEY);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey2 = keyFactory.generatePublic(publicKeySpec);
        return publicKey2;
    }

    private byte[] getKeyBytes(Class class_, String keyName) throws IOException {
        byte[] buffer = new byte[4096];
        int read = IOUtils.read(class_.getClassLoader().getResourceAsStream(keyName), buffer);
        byte[] publicKeyBytes = new byte[read];
        System.arraycopy(buffer, 0, publicKeyBytes, 0, read);
        return publicKeyBytes;
    }

    public String loadSignature() {
        File file = new File(signaturePath);
        if (file.exists()) {
            try {
                byte[] signatureData = Files.readAllBytes(file.toPath());
                return new String(signatureData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void saveSignature(String signature) throws IOException {
        File file = new File(signaturePath);
        if (file.exists()) {
            file.delete();
        }
        Files.write(file.toPath(), signature.getBytes(), StandardOpenOption.CREATE_NEW);
    }

    public KeyPair loadKeys4() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
        byte[] publicKeyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
        return loadKeyPair(privateKeyBytes, publicKeyBytes);
    }

    private KeyPair loadKeyPair(byte[] privateKeyBytes, byte[] publicKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey2 = keyFactory.generatePublic(publicKeySpec);
        return new KeyPair(publicKey2, privateKey2);
    }

    public KeyPair loadKeysFromResources(Class class_) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = getKeyBytes(class_, PUBLIC_KEY);
        byte[] privateKeyBytes = getKeyBytes(class_, PRIVATE_KEY);
        return loadKeyPair(privateKeyBytes, publicKeyBytes);
    }

    public KeyPair loadKeys() throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
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

    public KeyPair loadKeys2() throws URISyntaxException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] privateKeyBytes = Files.readAllBytes(new File(privateKeyPath).toPath());
        byte[] publicKeyBytes = Files.readAllBytes(new File(publicKeyPath).toPath());
        KeyFactory kf = KeyFactory.getInstance("RSA"); // or "EC" or whatever
        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        PublicKey publicKey = kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        return new KeyPair(publicKey, privateKey);
    }

    public KeyPair loadKeys3() {
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

    public byte[] sign(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature signature = Signature.getInstance("SHA1WithRSA");
        signature.initSign(privateKey);
        signature.update(data);
        byte[] signatureBytes = signature.sign();
        return signatureBytes;
    }

    public boolean verify(byte[] data, byte[] signatureBytes, PublicKey publicKey) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        Signature sig = Signature.getInstance("SHA1WithRSA");
        sig.initVerify(publicKey);
        sig.update(data);
        return sig.verify(signatureBytes);
    }

    public String sign(String plainText, PrivateKey privateKey) throws Exception {
        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(plainText.getBytes(UTF_8));
        byte[] signature = privateSignature.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    public boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(publicKey);
        publicSignature.update(plainText.getBytes(UTF_8));
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        return publicSignature.verify(signatureBytes);
    }
}
