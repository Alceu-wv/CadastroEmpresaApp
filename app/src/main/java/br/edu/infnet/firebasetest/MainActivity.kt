package br.edu.infnet.firebasetest

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.security.crypto.EncryptedFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File
import androidx.security.crypto.MasterKey
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), RecyclerViewItemListener {

    val REQUEST_IMAGE_CAPTURE = 1
    val WRITE_REQUEST = 2

    val criptografador = Criptografador()
    private lateinit var mUser: FirebaseUser
    private lateinit var empresaDAO: EmpresaDAO
    private lateinit var mAuth: FirebaseAuth

    private lateinit var txtName: EditText
    private lateinit var txtAdress: EditText
    private lateinit var txtComments: EditText
    private lateinit var checkBoxIsApproved: CheckBox
    private lateinit var btnSalvar: Button
    private lateinit var btnExit: Button
    private lateinit var btnFotografar: Button


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lstEmpresas = this.findViewById<RecyclerView>(R.id.lstEmpresas)
        lstEmpresas.layoutManager = LinearLayoutManager(this)
        val adapter = ListaEmpresaAdapter(this)
        lstEmpresas.adapter = adapter

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!

        this.atualizarLista()

        empresaDAO = EmpresaDAO()

        txtName = this.findViewById(R.id.txtName)
        txtAdress = this.findViewById(R.id.txtAdress)
        txtComments = this.findViewById(R.id.txtComments)
        checkBoxIsApproved = this.findViewById(R.id.checkBoxIsApproved)
        btnSalvar = this.findViewById(R.id.btnSalvar)
        btnExit = this.findViewById(R.id.btnExit)
        btnFotografar = this.findViewById(R.id.btnFotografar)

        empresaDAO.setUpEmpresaSnapshotListener { querySnapshot, firebaseFirestoreException ->
            Log.i("MainActivity", "------empresaDAO.setUpEmpresaSanpshotListener-------")
            if (querySnapshot != null) {
                for (documento in querySnapshot) {
                    var empresa = documento.toObject(Empresa::class.java)
                    Log.i("MainActivity", "${empresa.id} - ${empresa.name}")
                }
            }
        }

        btnExit.setOnClickListener { this.exit() }

        btnFotografar.setOnClickListener {
            this.takePicture()
        }

        btnSalvar.setOnClickListener {
            val empresa = Empresa()

            empresa.userId = mUser!!.uid
            empresa.name = txtName.text.toString()
            empresa.comments = txtComments.text.toString()
            empresa.adress = criptografador.criptografar(empresa.adress.toString())
            empresa.is_approved = checkBoxIsApproved.isChecked

            if (empresa?.name == null || empresa?.comments == null || empresa.adress == null) {
                Toast.makeText(this.applicationContext, "Preencha todos os campos", Toast.LENGTH_LONG)
            } else {
                empresaDAO.inserir(empresa)?.addOnSuccessListener {
                    Log.i("MainActivity", "Empresa inserida")
                }?.addOnFailureListener {
                    Log.i("MainActivity", "Erro ao inserir empresa")
                }

                txtAdress.text.clear()
                txtComments.text.clear()
                txtName.text.clear()

                this.atualizarLista()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun atualizarLista() {
        EmpresaDAO().obter_empresas_por_usuario(this.mUser.uid)!!.addOnSuccessListener { listaDeDocumentos ->

            val empresas = ArrayList<Empresa>()
            for(documento in listaDeDocumentos) {

                var empresa = documento.toObject(Empresa::class.java)
                empresa.adress = criptografador.descriptografar(empresa.adress!!.toString())
                empresas.add(empresa)
            }

            val lstEmpresas = this.findViewById<RecyclerView>(R.id.lstEmpresas)
            lstEmpresas.layoutManager = LinearLayoutManager(this)
            val adapter = ListaEmpresaAdapter(this)
            adapter.listaEmpresa = empresas
            adapter.setRecyclerViewItemListener(this)
            lstEmpresas.adapter = adapter

        }?.addOnFailureListener { exception ->

            Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()

        }
    }

    override fun editCLicked(view: View, position: Int, empresa: Empresa) {
        txtName.setText(empresa.name)
        txtAdress.setText(empresa.adress)
        txtComments.setText(empresa.comments)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun deleteClicked(view: View, position: Int, empresa: Empresa) {
        empresaDAO.deletar(empresa.id!!).addOnSuccessListener {
            Log.i("MainActivity", "Empresa ${empresa.name} deletada")
        }
        this.atualizarLista()
    }

    fun exit() {
        mAuth.signOut()
        val intent = Intent(this@MainActivity, Login::class.java)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun takePicture() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
        }
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun savePicture(bitmap: Bitmap?) {
        val data = Calendar.getInstance().time
        val dataFormat = SimpleDateFormat("dd_MM_yyyy_HH-mm-ss", Locale.getDefault())

        val masterKey = MasterKey.Builder(applicationContext, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val file = File(applicationContext.filesDir, "${mUser.uid}-${dataFormat.format(data)}.fig")

        if (file.exists()) {
            file.delete()
        }
        val encryptedFile = EncryptedFile.Builder(
            applicationContext,
            file,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
        val fos = encryptedFile.openFileOutput()
        fos.write(bitmap.toString().toByteArray())
        fos.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val pictureBitmap = data?.extras!!.get("data") as Bitmap
            val imgPictureView = this.findViewById<ImageView>(R.id.imageView)
            imgPictureView.setImageBitmap(pictureBitmap)
            savePicture(pictureBitmap)
        }
    }
}