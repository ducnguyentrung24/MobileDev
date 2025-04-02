package android.example.bai10

import androidx.activity.ComponentActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class MainActivity : ComponentActivity() {
    private lateinit var etServerIp: EditText
    private lateinit var etServerPort: EditText
    private lateinit var etMessage: EditText
    private lateinit var tvMessages: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnSend: Button

    private var clientSocket: Socket? = null
    private var writer: PrintWriter? = null
    private var reader: BufferedReader? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etServerIp = findViewById(R.id.etServerIp)
        etServerPort = findViewById(R.id.etServerPort)
        etMessage = findViewById(R.id.etMessage)
        tvMessages = findViewById(R.id.tvMessages)
        btnConnect = findViewById(R.id.btnConnect)
        btnSend = findViewById(R.id.btnSend)

        btnConnect.setOnClickListener {
            if (clientSocket == null) {
                connectToServer()
            } else {
                disconnectFromServer()
            }
        }

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                etMessage.text.clear()
            }
        }
    }

    private fun connectToServer() {
        val serverIp = etServerIp.text.toString().trim()
        val serverPortStr = etServerPort.text.toString().trim()

        if (serverIp.isEmpty() || serverPortStr.isEmpty()) {
            showToast("Please enter server IP and port")
            return
        }

        val serverPort = serverPortStr.toIntOrNull() ?: 9999

        coroutineScope.launch {
            try {
                // Connect to server
                clientSocket = Socket(serverIp, serverPort)
                writer = PrintWriter(clientSocket!!.getOutputStream(), true)
                reader = BufferedReader(InputStreamReader(clientSocket!!.getInputStream()))

                withContext(Dispatchers.Main) {
                    btnConnect.text = "Disconnect"
                    btnSend.isEnabled = true
                    appendMessage("Connected to server: $serverIp:$serverPort")
                }

                // Start listening for messages from the server
                while (clientSocket?.isClosed == false) {
                    try {
                        val message = reader?.readLine()
                        if (message != null) {
                            withContext(Dispatchers.Main) {
                                appendMessage("Server: $message")
                            }
                        } else {
                            // End of stream reached, server probably disconnected
                            break
                        }
                    } catch (e: Exception) {
                        break
                    }
                }

                // If we exited the loop, the connection is probably closed
                withContext(Dispatchers.Main) {
                    disconnectFromServer()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    appendMessage("Error: ${e.message}")
                    disconnectFromServer()
                }
            }
        }
    }

    private fun disconnectFromServer() {
        coroutineScope.launch {
            try {
                writer?.close()
                reader?.close()
                clientSocket?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                clientSocket = null
                writer = null
                reader = null
                btnConnect.text = "Connect"
                btnSend.isEnabled = false
                appendMessage("Disconnected from server")
            }
        }
    }

    private fun sendMessage(message: String) {
        if (clientSocket?.isConnected == true && writer != null) {
            coroutineScope.launch {
                try {
                    writer?.println(message)
                    withContext(Dispatchers.Main) {
                        appendMessage("Me: $message")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        appendMessage("Failed to send message: ${e.message}")
                    }
                }
            }
        } else {
            showToast("Not connected to server")
        }
    }

    private fun appendMessage(message: String) {
        tvMessages.append("$message\n\n")
        // Scroll to bottom
        val scrollView = tvMessages.parent as androidx.core.widget.NestedScrollView
        scrollView.post {
            scrollView.fullScroll(androidx.core.widget.NestedScrollView.FOCUS_DOWN)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.launch {
            try {
                writer?.close()
                reader?.close()
                clientSocket?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        coroutineScope.cancel()
    }
}
