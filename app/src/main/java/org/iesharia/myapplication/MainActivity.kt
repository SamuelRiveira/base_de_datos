package org.iesharia.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.iesharia.myapplication.ui.theme.MyApplicationTheme

// data, view, controller, MainActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                ) { innerPadding ->
                    MainActivity (
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@SuppressLint("Range")
@Composable
fun MainActivity(modifier: Modifier) {
    val context = LocalContext.current
    val db = DBHelper(context)

    val lName: MutableList<String> = remember { mutableStateListOf<String>() }
    val lAge: MutableList<String> = remember { mutableStateListOf<String>() }
    var nuevoNombre: String by remember { mutableStateOf("") }
    var nuevaEdad: String by remember { mutableStateOf("") }
    var indice: Int by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    val lId: MutableList<String> = remember { mutableStateListOf() }

    Column (
        verticalArrangement = Arrangement.Center,
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Base de Datos",
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Text(
            text = "Muuuuuy simple\nNombre/Edad",
            fontSize = 10.sp

        )
        //Nombre
        var nameValue by remember { mutableStateOf("") }
        OutlinedTextField(
            value = nameValue,
            onValueChange = {
                nameValue = it
            },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Nombre") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
        //Edad
        var ageValue by remember { mutableStateOf("") }
        OutlinedTextField(
            value = ageValue,
            onValueChange = {
                ageValue = it
            },
            modifier = Modifier,
            textStyle = TextStyle(color = Color.DarkGray),
            label = { Text(text = "Edad") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
        var bModifier:Modifier = Modifier.padding(16.dp)
        Row {
            Button(
                modifier = bModifier,
                onClick = {
                    val name = nameValue
                    val age = ageValue

                    db.addName(name, age)

                    Toast.makeText(
                        context,
                        name + " adjuntado a la base de datos",
                        Toast.LENGTH_LONG)
                        .show()

                    nameValue = ""
                    ageValue = ""
                }
            ) {
                Text(text = "Añadir")
            }
            Button(
                modifier = bModifier,
                onClick = {
                    try {
                        lId.clear()
                        lName.clear()
                        lAge.clear()

                        val db = DBHelper(context, null)

                        val cursor = db.getName()

                        if (cursor != null && cursor.moveToFirst()) {
                            do {
                                lId.add(cursor.getInt(cursor.getColumnIndex(DBHelper.ID_COL)).toString())
                                lName.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME_COl)))
                                lAge.add(cursor.getString(cursor.getColumnIndex(DBHelper.AGE_COL)))
                            } while (cursor.moveToNext())
                        }

                        cursor?.close()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            ) {
                Text(text = "Mostrar")
            }
        }
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Nombre")
                Spacer(modifier = Modifier.padding(16.dp))
                Text(text = "Edad")
            }
            for (i in lName.indices) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = bModifier,
                        text = lName[i]
                    )
                    Text(
                        modifier = bModifier,
                        text = lAge[i]
                    )
                    Image(
                        painter = painterResource(R.drawable.editar),
                        contentDescription = "Editar",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp)
                            .clickable{
                                showDialog = true
                                indice = i
                            }
                    )
                    Image(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp)
                            .clickable {
                                val dbHelper = DBHelper(context)
                                val id: String = lId[i]

                                dbHelper.deleteName(id)

                                lId.removeAt(i)
                                lName.removeAt(i)
                                lAge.removeAt(i)
                            }
                    )
                }
                if (showDialog){
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        title = { Text(text = "Editar") },
                        text = {
                            Column {
                                TextField(
                                    value = nuevoNombre,
                                    onValueChange = { nuevoNombre = it },
                                    placeholder = { Text(text = lName[indice]) }
                                )
                                Spacer(
                                    modifier = Modifier
                                        .height(8.dp)
                                )
                                TextField(
                                    value = nuevaEdad,
                                    onValueChange = { nuevaEdad = it },
                                    placeholder = { Text(text = lAge[indice]) }
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    val dbHelper = DBHelper(context)
                                    val id = lId[indice]

                                    val rowsAffected = dbHelper.updateName(id, nuevoNombre, nuevaEdad)
                                    if (rowsAffected > 0) {
                                        lName[indice] = nuevoNombre
                                        lAge[indice] = nuevaEdad
                                    }

                                    showDialog = false
                                }
                            ) {
                                Text("Aceptar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}
