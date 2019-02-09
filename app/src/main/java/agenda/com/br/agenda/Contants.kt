package agenda.com.br.agenda

import android.content.BroadcastReceiver
import java.text.SimpleDateFormat

object Contants {

    const val CONTATOS_TABLE_DATABASE = "contatos.db"
    const val CONTATOS_TABLE_NAME = "agenda"
    var dateFormatter = SimpleDateFormat("dd/MM/yyyy")
    const val REQUEST_IMAGE_CAPTURE = 1
    val MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 10
    var receiver: BroadcastReceiver? = null

}