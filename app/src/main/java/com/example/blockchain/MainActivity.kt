package com.example.blockchain

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.blockchain.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToLong


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val contractAddress = "0x3caeb113c91d9475646e0c08ce81cf0a9cb3c612"
    private val privateKey =
        Credentials.create("47765422c2fdbd8f13377e20d90da1785b453d3d5aa682c851ebe825735164a9")
    private val url = "https://sepolia.infura.io/v3/81772628ac8649babbc528a8a4d3d376"

    private val accountAddress = "0xE4AC1a31D59BA897091620E3F3d0e69520887B8c"
    private val transactionHash =
        "0x746b8d4f614cbdd37e761d7bf24223635e99a13c8ef153b90c576c07b37549f9"
//    "0x686806ed7b3166bb827321980471f2083ee5f9d7635a688f83b1120a6002597b"
//    "0x2f334ec288ad6bb0a38eabe208efe7e363484d18dc3e9478b2ab4f393b338336"
//    "0x506356ddf17d95cf6a2dc65650262064198cca829fa68ae0fd9fc62f3083a308"

    private val transactionMsg = "test transaction"
    private val dataMsg = "Gas pipeline work at kadavanthra"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getATransaction.setOnClickListener {
            Toast.makeText(this, "Fetching details of a transaction...", Toast.LENGTH_SHORT).show()
            val postData =
                "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionByHash\",\"params\": [\"${transactionHash}\"],\"id\":1}"
            postRequest(postData, "transaction")
        }

        binding.getTotalTransactionCount.setOnClickListener {
            Toast.makeText(this, "Fetching total transaction count...", Toast.LENGTH_SHORT).show()
            val postData =
                "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionCount\",\"params\": [\"${accountAddress}\",\"latest\"],\"id\":1}"
            postRequest(postData, "count")
        }

        binding.getWalletBalance.setOnClickListener {
            val postData =
                "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\": [\"${accountAddress}\", \"latest\"],\"id\":1}"
            postRequest(postData, "count")
        }

        binding.performATransaction.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val web3j = Web3j.build(HttpService(url))
                    val helloWorld: Data_sol_Data = Data_sol_Data.deploy(
                        web3j,
                        privateKey,
                        DefaultGasProvider()
                    ).send()
                    helloWorld.addRecord(transactionMsg).send()
                    Log.d("TEST", "Success")
                    web3j.shutdown()
                } catch (e: Exception) {
                    Log.d("ERR", e.toString())
                }
            }
        }

        binding.btdAddData.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val web3j = Web3j.build(HttpService(url))

//                    val toAddress = "0xRecipientAddress"
//                    val value = BigDecimal("0.1")  // Amount of Ether to send
//                    val data = "Hello, Ethereum!"  // Message to attach
//
//                    val transaction = RawTransaction.createTransaction(
//                        BigInteger("30000"),
//                        null,
//                        BigInteger("30000"),
//                        contractAddress,
//                        value.toBigInteger(),
//                        data,
//                    )
//                    val signedTransaction = TransactionEncoder.signMessage(transaction, privateKey)
//                    val hexValue = Numeric.toHexString(signedTransaction)
//                    val ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send()
//
//                    if (ethSendTransaction.hasError()) {
//                        val error = ethSendTransaction.error.message
//                        Log.d("TEST",error)
//                    } else {
//                        val transactionHash = ethSendTransaction.transactionHash
//                        Log.d("TEST",transactionHash)
//
//                        // Transaction successful, do something with the transaction hash
//                    }


                    val contract: Data_sol_Data = Data_sol_Data.load(
                        contractAddress,
                        web3j,
                        privateKey,
                        DefaultGasProvider()
                    )
                    if (contract.isValid) {
                        contract.addRecord(dataMsg).send()
                        Log.d("TEST", "DATA added successfully!!")
                    }
                    web3j.shutdown()
                } catch (e: Exception) {
                    Log.d("ERR", e.toString())
                }
            }
        }
    }


    private fun postRequest(postData: String, action: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = URL("https://sepolia.infura.io/v3/81772628ac8649babbc528a8a4d3d376")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            conn.setRequestProperty("Content-Length", postData.length.toString())
            conn.useCaches = false

            DataOutputStream(conn.outputStream).use { it.writeBytes(postData) }
            BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
                var value: String?
                var result = ""
                while (br.readLine().also { value = it } != null) {
                    println(value)
                    val jsonObject = JSONObject(value!!)
                    if (action == "count") {
                        var data = removeTrailingZeroes(jsonObject.getString("result"))
                        data = data.subSequence(2, data.length).toString()
                        result = hexToDecimal(data).toString()
                        if(result.length>5){
                            var strBldr = StringBuilder(result)
                            strBldr.insert(1,'.')
                            result = strBldr.toString()+" ETH"
                        }else{
                            result = "Transaction Count: "+result
                        }

                    } else {
                        val data = JSONObject(jsonObject.getString("result"))
                        val fromAddress = data.getString("from")
                        val toAddress = data.getString("to")
                        var gas = data.getString("gas")
                        gas = gas.subSequence(2, gas.length).toString()
                        gas = hexToDecimal(gas).toString()
                        var gasPrice = data.getString("gasPrice")
                        gasPrice = gasPrice.subSequence(2, gasPrice.length).toString()
                        gasPrice = hexToDecimal(gasPrice).toString()
                        gasPrice = gasPrice.substring(0,3)
                        var gp = gasPrice.toFloat()
                        gp /= 100
                        gasPrice = gp.toString()
                        var msg = data.getString("input")
                        msg = msg.substring(
                            msg.length - 64,
                            msg.length
                        )
                        msg = removeTrailingZeroes(msg)
                        msg = hexToAscii(msg)
                        Log.d("MSG", msg)
                        result =
                            "From: ${fromAddress}\nTo: ${toAddress}\nGas: ${gas}\nGas price: ${gasPrice} Gwei\nMessage: $msg"
                    }
                }
                runOnUiThread {
                    binding.resultText.text = result
                }
            }
        }
    }

    private fun removeTrailingZeroes(s: String?): String {
        val sb = java.lang.StringBuilder(s.toString())
        while (sb.isNotEmpty() && sb[sb.length - 1] == '0') {
            sb.setLength(sb.length - 1)
        }
        return sb.toString()
    }

    private fun hexToAscii(hexStr: String): String {
        val output = StringBuilder("")
        var i = 0
        while (i < hexStr.length) {
            val str: String = hexStr.substring(i, i + 2)
            output.append(str.toInt(16).toChar())
            i += 2
        }
        return output.toString()
    }

    private fun hexToDecimal(hex: String): Long {
        return hex.toLong(16)
    }
}