package com.huahui.datasphere.portal.security.jwt;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.huahui.datasphere.portal.configuration.DSSConf;


public class KeyManager
{
    protected static final Logger logger;
    
    public static void generateRSAKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
        KeyManager.logger.info("Init keys generation ...");
        Security.addProvider((Provider)new BouncyCastleProvider());
        final KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(1024, new SecureRandom());
        final KeyPair keyPair = generator.generateKeyPair();
        final RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
        final String publicKeyFile = DSSConf.getConfig().getString("publickey_path", null);
        createKeystore(keyPair);
        storeKeyToFile(publicKey, publicKeyFile, "RSA PUBLIC KEY");
        KeyManager.logger.info("RSA key pair generated and stored.");
    }
    
    private static void storeKeyToFile(final Key key, final String fileName, final String desc) throws IOException {
        final PemObject pemObject = new PemObject(desc, key.getEncoded());
        final PemWriter pemWriter = new PemWriter((Writer)new OutputStreamWriter(new FileOutputStream(fileName)));
        try {
            pemWriter.writeObject((PemObjectGenerator)pemObject);
        }
        finally {
            pemWriter.close();
        }
    }
    
    public static PrivateKey getPrivateKeyFromFile(final String filename) throws InvalidKeySpecException, FileNotFoundException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider((Provider)new BouncyCastleProvider());
        final KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        final byte[] content = readKey(filename).getContent();
        final PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(content);
        return factory.generatePrivate(privKeySpec);
    }
    
    public static PublicKey getPublicKeyFromFile(final String filename) throws InvalidKeySpecException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        Security.addProvider((Provider)new BouncyCastleProvider());
        final KeyFactory factory = KeyFactory.getInstance("RSA", "BC");
        final byte[] content = readKey(filename).getContent();
        final X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
        return factory.generatePublic(pubKeySpec);
    }
    
    private static PemObject readKey(final String filename) throws IOException {
        final PemReader pemReader = new PemReader((Reader)new InputStreamReader(new FileInputStream(filename)));
        try {
            return pemReader.readPemObject();
        }
        finally {
            pemReader.close();
        }
    }
    
    private static void createKeystore(final KeyPair keyPair) {
        try {
            final X509Certificate[] chain = { null };
            final X500Name issuer = new X500Name("C=IN,O=Infoworks,CN=ROOT");
            final BigInteger serialNumber = BigInteger.valueOf(System.currentTimeMillis());
            final Date startDate = new Date();
            final Date expiryDate = new Date(System.currentTimeMillis() - 1352509440L);
            final X500Name subject = new X500Name("T=Infoworks_Cube");
            final X509v3CertificateBuilder certBuilder = new X509v3CertificateBuilder(issuer, serialNumber, startDate, expiryDate, subject, SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));
            final JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256withRSA");
            final ContentSigner signer = builder.build(keyPair.getPrivate());
            final byte[] certBytes = certBuilder.build(signer).getEncoded();
            final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            final X509Certificate certificate = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(certBytes));
            chain[0] = certificate;
            final String keystoreType = DSSConf.getConfig().getString("keystore_type", "jks");
            final String keystoreFile = DSSConf.getConfig().getString("keystore_path", null);
            final String alias = DSSConf.getConfig().getString("keystore_entry_alias", "dss-cube");
            final String keystorePassword = DSSConf.getConfig().getString("keystore_password", "datasphere");
            final String keyPassword = DSSConf.getConfig().getString("keystore_key_password", "datasphere");
            KeyManager.logger.info("Creating keystore file: " + keystoreFile);
            storeKeyAndCertificateChain(keystoreType, keystorePassword, alias, keystoreFile, keyPair.getPrivate(), keyPassword, chain);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void storeKeyAndCertificateChain(final String keystoreType, final String keystorePass, final String alias, final String keystoreFile, final Key key, final String keyPass, final X509Certificate[] chain) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        final KeyStore keyStore = KeyStore.getInstance(keystoreType);
        keyStore.load(null, null);
        keyStore.setKeyEntry(alias, key, keyPass.toCharArray(), chain);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(keystoreFile);
            keyStore.store(fos, keystorePass.toCharArray());
            KeyManager.logger.info("Successfully stored private key and certificate in keystore.");
        }
        finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
    
    public static Key getPrivateKeyFromKeystore(final String keystoreType, final String alias, final String keystorePass, final String keystoreFile, final String keyPass) throws Exception {
        Security.addProvider((Provider)new BouncyCastleProvider());
        final KeyStore keyStore = KeyStore.getInstance(keystoreType);
        keyStore.load(new FileInputStream(keystoreFile), keystorePass.toCharArray());
        final Key key = keyStore.getKey(alias, keyPass.toCharArray());
        if (key instanceof PrivateKey) {
            KeyManager.logger.info("retrieving private key.");
            return key;
        }
        KeyManager.logger.info("Key is not a private key");
        return null;
    }
    
    public static PrivateKey getPrivateKeyFromKeystore(final String keystoreFile) throws Exception {
        Security.addProvider((Provider)new BouncyCastleProvider());
        final String keystoreType = DSSConf.getConfig().getString("keystore_type", "jks");
        final String alias = DSSConf.getConfig().getString("keystore_entry_alias", "dss-cube");
        final String keystorePassword = DSSConf.getConfig().getString("keystore_password", "datasphere");
        final String keyPassword = DSSConf.getConfig().getString("keystore_key_password", "datasphere");
        final KeyStore keyStore = KeyStore.getInstance(keystoreType);
        keyStore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());
        return (PrivateKey)keyStore.getKey(alias, keyPassword.toCharArray());
    }
    
    private static Certificate[] getCertificate(final String keystoreType, final String alias, final String keystorePass, final String keystoreFile, final String keyPass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        Security.addProvider((Provider)new BouncyCastleProvider());
        final KeyStore keyStore = KeyStore.getInstance(keystoreType);
        keyStore.load(new FileInputStream(keystoreFile), keystorePass.toCharArray());
        final Key key = keyStore.getKey(alias, keyPass.toCharArray());
        if (key instanceof PrivateKey) {
            return keyStore.getCertificateChain(alias);
        }
        KeyManager.logger.info("Key is not a private key");
        return null;
    }
    
    private static void clearKeyStoreEntry(final String keystoreType, final String alias, final String keystorePass, final String keystoreFile) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        final KeyStore keyStore = KeyStore.getInstance(keystoreType);
        keyStore.load(new FileInputStream(keystoreFile), keystorePass.toCharArray());
        keyStore.deleteEntry(alias);
        keyStore.store(new FileOutputStream(keystoreFile), keystorePass.toCharArray());
        KeyManager.logger.info("removed alias: '" + alias + "' from keystore");
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)KeyManager.class);
    }
}
