package agenda.com.br.agenda

import agenda.com.br.agenda.Contants.MY_PERMISSIONS_REQUEST_SMS_RECEIVE
import agenda.com.br.agenda.Contants.receiver
import agenda.com.br.agenda.broadcast.SMSReceiver
import agenda.com.br.agenda.db.Contato
import agenda.com.br.agenda.db.ContatoRepository
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import android.graphics.Color
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.telephony.SmsMessage
import android.util.Log
import android.view.ContextMenu
import kotlinx.android.synthetic.main.activity_lista_contatos.*
import android.widget.ArrayAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import java.util.jar.Manifest
import java.util.logging.Logger

class ListaContatosActivity : AppCompatActivity() {

    private var contatos:ArrayList<Contato>? = null
    private var contatoSelecionado:Contato? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_contatos);

        val myToolbar = toolbar
        myToolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(myToolbar)

        contatos = ContatoRepository(this).findAll()
        val adapter= ArrayAdapter(this, android.R.layout.simple_list_item_1, contatos)
        lista?.adapter = adapter
        adapter.notifyDataSetChanged()

        lista.setOnItemClickListener { _, _, position, id ->
            val intent = Intent(this@ListaContatosActivity, ContatoActivity::class.java)
            intent.putExtra("contato", contatos?.get(position))
            startActivity(intent)
        }

        lista.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapter, view, posicao, id ->
            contatoSelecionado = contatos?.get(posicao)
            false
        }

        setupPermissions()
        configureReceiver()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.novo -> {
                val intent = Intent(this, ContatoActivity::class.java)
                startActivity(intent)
                return false
            }

            R.id.sincronizar -> {
                Toast.makeText(this, "Enviar", Toast.LENGTH_LONG).show()
                return false
            }

            R.id.receber -> {
                Toast.makeText(this, "Receber", Toast.LENGTH_LONG).show()
                return false
            }

            R.id.mapa -> {
                Toast.makeText(this, "Mapa", Toast.LENGTH_LONG).show()
                return false
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        menuInflater.inflate(R.menu.menu_contato_contexto, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.excluir -> {
                AlertDialog.Builder(this@ListaContatosActivity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Deletar")
                        .setMessage("Deseja mesmo deletar ?")
                        .setPositiveButton("Quero",
                                DialogInterface.OnClickListener { dialog, which ->
                                    ContatoRepository(this).delete(this.contatoSelecionado!!.id)
                                    carregaLista()
                                }).setNegativeButton("Nao", null).show()
                return false
            }
            else -> return super.onContextItemSelected(item)
        }
    }

    private fun carregaLista() {
        contatos = ContatoRepository(this).findAll()
        val adapter= ArrayAdapter(this, android.R.layout.simple_list_item_1, contatos)
        lista?.adapter = adapter
        adapter.notifyDataSetChanged()
    }



    override fun onResume() {
        super.onResume()
        contatos = ContatoRepository(this).findAll()
        val adapter= ArrayAdapter(this, android.R.layout.simple_list_item_1, contatos)
        lista?.adapter = adapter
        adapter.notifyDataSetChanged()
        registerForContextMenu(lista);
    }

    private fun setupPermissions() {

        val list = listOf<String>(
                android.Manifest.permission.RECEIVE_SMS
        )

        ActivityCompat.requestPermissions(this,
                list.toTypedArray(), MY_PERMISSIONS_REQUEST_SMS_RECEIVE);

        val permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_SMS)

        if (permission != PackageManager.GET_SERVICES) {
            Log.i("aula", "Permission to record denied")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            Logger.getLogger(SmsMessage::class.java.name).warning("Permission RECEIVE SMS")
        }
    }

    private fun configureReceiver() {
        val filter = IntentFilter()
        filter.addAction("br.aula.agenda.broadcast.SMSreceiver")
        filter.addAction("android.provider.Telephony.SMS_RECEIVED")
        receiver = SMSReceiver()
        registerReceiver(receiver, filter)
    }



}

