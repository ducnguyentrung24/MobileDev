package android.example.bai11

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class MainActivity : AppCompatActivity() {

    private lateinit var etTargetIp: EditText
    private lateinit var etTargetPort: EditText
    private lateinit var etLocalPort: EditText
    private lateinit var etMessage: EditText
    private lateinit var tvMessages: TextView
    private lateinit var btnSend: Button
    private lateinit var btnListen: Button

    private var receiveSocket: DatagramSocket? = null
    private var isListening = false
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etTargetIp = findViewById(R.id.etTargetIp)
        etTargetPort = findViewById(R.id.etTargetPort)
        etLocalPort = findViewById(R.id.etLocalPort)
        etMessage = findViewById(R.id.etMessage)
        tvMessages = findViewById(R.id.tvMessages)
        btnSend = findViewById(R.id.btnSend)
        btnListen = findViewById(R.id.btnListen)

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                etMessage.text.clear()
            } else {
                showToast("Please enter a message")
            }
        }

        btnListen.setOnClickListener {
            if (isListening) {
                stopListening()
            } else {
                startListening()
            }
        }
    }

    private fun sendMessage(message: String) {
        val targetIp = etTargetIp.text.toString().trim()
        val targetPortStr = etTargetPort.text.toString().trim()

        if (targetIp.isEmpty() || targetPortStr.isEmpty()) {
            showToast("Please enter target IP and port")
            return
        }

        val targetPort = targetPortStr.toIntOrNull() ?: 9000

        coroutineScope.launch {
            try {
                // Create socket for sending (ephemeral port)
                val socket = DatagramSocket()

                // Convert message to bytes
                val messageBytes = message.toByteArray()

                // Create the packet with data, target IP and port
                val packet = DatagramPacket(
                    messageBytes,
                    messageBytes.size,
                    InetAddress.getByName(targetIp),
                    targetPort
                )

                // Send the packet
                socket.send(packet)

                // Close the socket
                socket.close()

                withContext(Dispatchers.Main) {
                    appendMessage("Me: $message")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    appendMessage("Error sending message: ${e.message}")
                }
            }
        }
    }

    private fun startListening() {
        val localPortStr = etLocalPort.text.toString().trim()
        if (localPortStr.isEmpty()) {
            showToast("Please enter local port")
            return
        }

        val localPort = localPortStr.toIntOrNull() ?: 9000

        coroutineScope.launch {
            try {
                // Create socket for listening on specific port
                receiveSocket = DatagramSocket(localPort)
                isListening = true

                withContext(Dispatchers.Main) {
                    btnListen.text = "Stop Listening"
                    appendMessage("Started listening on port $localPort")
                }

                // Keep receiving until stopped
                while (isListening) {
                    try {
                        // Buffer for incoming data
                        val buffer = ByteArray(1024)
                        val packet = DatagramPacket(buffer, buffer.size)

                        // Wait for incoming packet (blocks until data received)
                        receiveSocket?.receive(packet)

                        // Extract message from the packet
                        val message = String(packet.data, 0, packet.length)
                        val sender = packet.address.hostAddress

                        withContext(Dispatchers.Main) {
                            appendMessage("$sender: $message")
                        }
                    } catch (e: Exception) {
                        if (isListening) {
                            e.printStackTrace()
                            withContext(Dispatchers.Main) {
                                appendMessage("Error receiving message: ${e.message}")
                            }
                        }
                        // If socket closed, break the loop
                        if (receiveSocket?.isClosed == true) {
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    appendMessage("Error starting listener: ${e.message}")
                    stopListening()
                }
            }
        }
    }

    private fun stopListening() {
        isListening = false
        coroutineScope.launch {
            try {
                receiveSocket?.close()
                receiveSocket = null

                withContext(Dispatchers.Main) {
                    btnListen.text = "Start Listening"
                    appendMessage("Stopped listening")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun appendMessage(message: String) {
        tvMessages.append("$message\n\n")
        // Scroll to bottom
        val scrollView = tvMessages.parent as android.widget.ScrollView
        scrollView.post {
            scrollView.fullScroll(android.widget.ScrollView.FOCUS_DOWN)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        isListening = false
        coroutineScope.launch {
            receiveSocket?.close()
        }
        coroutineScope.cancel()
    }
}