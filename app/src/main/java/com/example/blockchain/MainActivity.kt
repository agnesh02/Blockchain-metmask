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
import java.net.HttpURLConnection
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val contractAddress = "0x3caeb113c91d9475646e0c08ce81cf0a9cb3c612"
    private val privateKey =
        Credentials.create("47765422c2fdbd8f13377e20d90da1785b453d3d5aa682c851ebe825735164a9")
    private val url = "https://sepolia.infura.io/v3/81772628ac8649babbc528a8a4d3d376"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getATransaction.setOnClickListener {
            Toast.makeText(this, "Fetching details of a transaction...", Toast.LENGTH_SHORT).show()
            val postData =
//                "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionReceipt\",\"params\": [\"0x686806ed7b3166bb827321980471f2083ee5f9d7635a688f83b1120a6002597b\"],\"id\":1}"
                "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionByHash\",\"params\": [\"0x2f334ec288ad6bb0a38eabe208efe7e363484d18dc3e9478b2ab4f393b338336\"],\"id\":1}"
            postRequest(postData, "transaction")

        }

        binding.getTotalTransactionCount.setOnClickListener {
            Toast.makeText(this, "Fetching total transaction count...", Toast.LENGTH_SHORT).show()
            val postData =
                "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getTransactionCount\",\"params\": [\"0xE4AC1a31D59BA897091620E3F3d0e69520887B8c\",\"latest\"],\"id\":1}"
            postRequest(postData, "count")
        }

        binding.getWalletBalance.setOnClickListener {
            val postData =
                "{\"jsonrpc\":\"2.0\",\"method\":\"eth_getBalance\",\"params\": [\"0xE4AC1a31D59BA897091620E3F3d0e69520887B8c\", \"latest\"],\"id\":1}"
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
                    Log.d("TEST", helloWorld.contractAddress)
                    helloWorld.addRecord("test").send()
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
                    val contract: Data_sol_Data = Data_sol_Data.load(
                        contractAddress,
                        web3j,
                        privateKey,
                        DefaultGasProvider()
                    )
                    if (contract.isValid) {
                        contract.addRecord("Road work at vytilla").send()
                        Log.d("TEST", contract.getnumberOfRecords().send().toString())
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
                    val jsonObject = JSONObject(value)
                    if (action.equals("count")) {
                        var data = removeTrailingZeroes(jsonObject.getString("result"))
                        data = data.subSequence(2, data.length).toString()
                        result = hexToDecimal(data).toString()

                    } else {
                        val data = JSONObject(jsonObject.getString("result"))
                        val fromAddress = data.getString("from")
                        val toAddress = data.getString("to")
                        val gas = data.getString("gas")
                        val gasPrice = data.getString("gasPrice")
                        result =
                            "From: ${fromAddress}\nTo: ${toAddress}\ngas: ${gas}\ngasPrice: $gasPrice"
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

    private fun hexToAscii(hexStr: String) {
        val output = StringBuilder("")
        var i = 0
        while (i < hexStr.length) {
            val str: String = hexStr.substring(i, i + 2)
            output.append(str.toInt(16).toChar())
            i += 2
        }
        Log.d("DATA", output.toString())
    }

    private fun hexToDecimal(hex: String): Long {
        return hex.toLong(16)
    }
}