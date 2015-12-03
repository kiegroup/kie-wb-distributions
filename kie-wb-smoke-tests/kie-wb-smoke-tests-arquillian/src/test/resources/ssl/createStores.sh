#!/bin/sh -xe

keytool=/usr/lib/jvm/java-1.6.0-sun-1.6.0.45.x86_64/jre/bin/keytool
serverKeyPass=SERVER_KEYSTORE_PASSWORD
serverTrustPass=SERVER_TRUSTSTORE_PASSWORD
clientKeyPass=CLIENT_KEYSTORE_PASSWORD

mkdir -p server
serverKeystore=server/keystore.jks
clientKeystore=client_keystore.jks
serverTruststore=server/truststore.jts
clientCertificate=client.cer
serverCertificate=server.cer

clientAlias=client
serverAlias=server

#type=pkcs12
type=jks

rm -f $clientCertificate $serverCertificate
rm -f $serverKeystore $clientKeystore $serverTruststore

# create client keystore
$keytool -genkey -keystore $clientKeystore -keyalg RSA -keysize 2048 -alias $clientAlias -storepass $clientKeyPass -storetype $type

# create client certficate from client keystore
$keytool -exportcert -keystore $clientKeystore -storetype $type -storepass $clientKeyPass -alias $clientAlias -file $clientCertificate

# create server keystore
$keytool -genkey -alias $serverAlias -keyalg RSA -keystore $serverKeystore -storepass $serverKeyPass -storetype $type

# create server certficate from server keystore
$keytool -exportcert -keystore $serverKeystore -storetype $type -storepass $serverKeyPass -alias $serverAlias -file $serverCertificate


# create server trustore and import client certificate into it
$keytool -import -file $clientCertificate -alias $clientAlias -keystore $serverTruststore -storepass $serverTrustPass

# import server certificate into client keystore
$keytool -import -file $serverCertificate -alias $serverAlias -keystore $clientKeystore -storepass $clientKeyPass

# list certificates
echo "--- SERVER: ";
$keytool -list -v -keystore $serverTruststore -storepass $serverTrustPass
echo "--- CLIENT: ";
$keytool -list -v -keystore $clientKeystore -storepass $clientKeyPass

testDir=../../../../target/test-classes/ssl
rm -rf $testDir
mkdir $testDir
cp -r * $testDir

rm -f $clientCertificate $serverCertificate
