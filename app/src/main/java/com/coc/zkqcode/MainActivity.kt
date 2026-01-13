package com.coc.zkqcode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.coc.zkqcode.utils.CheckRootScreen
import com.coc.zkqcode.zkqnative.NativeTools

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            CheckRootScreen()
        }

        // Demo of Chacha20 and Blake2b
        val key = "71c72c74d062b7e7eebef849b7999790f64930af7688a661f79ced65a8917" // 32 bytes hex
        val nonce = "000000000000004a00000000" // 12 bytes hex
        val message = "Hello, Monocypher!"

        val encrypted = NativeTools.chacha20Encrypt(message, key, nonce)
        val decrypted = NativeTools.chacha20Decrypt(encrypted, key, nonce)
        val hash = NativeTools.blake2b(message)

        println("ZK_NATIVE: Message: $message")
        println("ZK_NATIVE: Encrypted (Hex): $encrypted")
        println("ZK_NATIVE: Decrypted: $decrypted")
        println("ZK_NATIVE: Blake2b Hash: $hash")
        
        // Demo of Key Exchange
        // For demo purposes, we'll split the keypair string which comes as "public_hex,private_hex"
        val aliceKeyPair = NativeTools.generateX25519KeyPair().split(",")
        val bobKeyPair = NativeTools.generateX25519KeyPair().split(",")
        
        val alicePub = aliceKeyPair[0]
        val alicePriv = aliceKeyPair[1]
        val bobPub = bobKeyPair[0]
        val bobPriv = bobKeyPair[1]
        
        val sharedAlice = NativeTools.computeSharedSecret(alicePriv, bobPub)
        val sharedBob = NativeTools.computeSharedSecret(bobPriv, alicePub)
        
        println("ZK_NATIVE: Shared Secret Alice: $sharedAlice")
        println("ZK_NATIVE: Shared Secret Bob:   $sharedBob")
        println("ZK_NATIVE: Secrets Match: ${sharedAlice == sharedBob}")
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

