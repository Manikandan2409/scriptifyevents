package com.heremanikandan.scriptifyevents.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage



 fun sendOTP(email: String, otp:String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val senderEmail = "scriptifyevents@gmail.com"
            val senderPassword = "egjf mqvu capb rgqx"

            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            val session = Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, senderPassword)
                }
            })

            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
                    subject = "Your OTP Verification Code"
                    setText("Your OTP code is: $otp. This code is valid for 2 minutes.")
                }

                Transport.send(message)
                println("OTP Sent Successfully!")
                onSuccess() // Call success callback
            } catch (e: Exception) {
                e.printStackTrace()
                onFailure(e) // Call failure callback
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onFailure(e) // Call failure callback
        }
    }
}
fun generateOtp():String{
    val otp = (100000..999999).random().toString() // Generate 6-digit OTP
    return  otp
}



