
package com.example.taskmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Feladat(
    val id: Int,
    val nev: String,
    val kategoria: String,
    val prioritas: Int,
    val kesz: Boolean = false
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                FeladatListaScreen()
            }
        }
    }
}

@Composable
fun FeladatListaScreen() {

    val context = LocalContext.current

    var nev by remember {
        mutableStateOf("")
    }

    var kategoria by remember {
        mutableStateOf("")
    }

    var prioritas by remember {
        mutableStateOf("1")
    }

    var nextId by remember {
        mutableStateOf(1)
    }

    val feladatok = remember {
        mutableStateListOf<Feladat>()
    }

    var nevError by remember {
        mutableStateOf(false)
    }

    var kategoriaError by remember {
        mutableStateOf(false)
    }

    var prioritasError by remember {
        mutableStateOf(false)
    }

    var szuro by remember {
        mutableStateOf("Összes")
    }

    val szurtFeladatok = feladatok.filter { feladat ->
        when (szuro) {
            "Aktív" -> !feladat.kesz
            "Elkészült" -> feladat.kesz
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Text(
            text = "TaskManager",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text("Napi teendők nyilvántartása")

        OutlinedTextField(
            value = nev,
            onValueChange = {
                nev = it
                nevError = false
            },
            label = { Text("Feladat neve: ") },
            supportingText = {
                if (nevError) {
                    Text(
                        "Feladat nevét kötelező megadni",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = kategoria,
            onValueChange = {
                kategoria = it
                kategoriaError = false
            },
            label = { Text("Kategória: ") },
            supportingText = {
                if (kategoriaError) {
                    Text(
                        "Kategóriát kötelező megadni",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = prioritas,
            onValueChange = {
                prioritas = it
                prioritasError = false
            },
            label = { Text("Prioritás (1-3)") },
            supportingText = {
                if (prioritasError) {
                    Text(
                        "A prioritás 1től 3ig lehet",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {

                nevError = nev.isBlank()
                kategoriaError = kategoria.isBlank()
                val prioritasSzam = prioritas.toIntOrNull()

                prioritasError = prioritasSzam == null ||prioritasSzam < 1 || prioritasSzam > 3

                if (nevError || kategoriaError || prioritasError) {

                    Toast.makeText(
                        context,
                        "Minden adatot meg kell adni",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    val ujFeladat = Feladat(
                        id = nextId,
                        nev = nev.trim(),
                        kategoria = kategoria.trim(),
                        prioritas = prioritasSzam!!
                    )

                    feladatok.add(ujFeladat)
                    nextId++

                    Toast.makeText(
                        context,
                        "Feladat hozzáadva",
                        Toast.LENGTH_SHORT
                    ).show()

                    nev = ""
                    kategoria = ""
                    prioritas = "1"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hozzáadás")
        }

        HorizontalDivider()

        Text(
            text = "Szűrés",
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Button(
                onClick = { szuro = "Összes" },
                modifier = Modifier.weight(1f)
            ) {
                Text("Összes")
            }

            Button(
                onClick = { szuro = "Aktív" },
                modifier = Modifier.weight(1f)
            ) {
                Text("Aktív")
            }

            Button(
                onClick = { szuro = "Elkészült" },
                modifier = Modifier.weight(1f)
            ) {
                Text("Kész")
            }
        }

        Text("Feladatok száma: ${szurtFeladatok.size}")

        if (szurtFeladatok.isEmpty()) {

            Text(
                text = "Nincs megjeleníthető feladat.",
                style = MaterialTheme.typography.bodyLarge
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = szurtFeladatok,
                    key = { feladat -> feladat.id }
                ) { feladat ->

                    FeladatCard(
                        feladat = feladat,

                        onDelete = {
                            feladatok.remove(feladat)
                        },

                        onEdit = { modositottFeladat ->

                            val index = feladatok.indexOfFirst {
                                it.id == feladat.id
                            }

                            if (index != -1) {
                                feladatok[index] = modositottFeladat
                            }
                        },

                        onKesz = {

                            val index = feladatok.indexOfFirst {
                                it.id == feladat.id
                            }

                            if (index != -1) {
                                feladatok[index] = feladat.copy(
                                    kesz = !feladat.kesz
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FeladatCard(feladat: Feladat, onDelete: () -> Unit, onEdit: (Feladat) -> Unit,
                onKesz: () -> Unit)
{

    val context = LocalContext.current

    var torlesDialog by remember {
        mutableStateOf(false)
    }

    var szerkesztesDialog by remember {
        mutableStateOf(false)
    }

    var editNev by remember {
        mutableStateOf(feladat.nev)
    }

    var editKategoria by remember {
        mutableStateOf(feladat.kategoria)
    }

    var editPrioritas by remember {
        mutableStateOf(feladat.prioritas.toString())
    }

    var editnevError by remember {
        mutableStateOf(false)
    }

    var editkategoriaError by remember {
        mutableStateOf(false)
    }

    var editprioritasError by remember {
        mutableStateOf(false)
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                FeladatAvatar(feladat.nev)

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = feladat.nev,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text("Kategória: ${feladat.kategoria}")

                    Text("Prioritás: ${feladat.prioritas}")

                    Text(
                        text = if (feladat.kesz) {
                            "Állapot: Elkészült"
                        } else {
                            "Állapot: Aktív"
                        }
                    )
                }

                Column {

                    IconButton(
                        onClick = {
                            torlesDialog = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.deleteicon
                            ),
                            contentDescription = "Feladat törlése"
                        )
                    }

                    IconButton(
                        onClick = {
                            editNev = feladat.nev
                            editKategoria = feladat.kategoria
                            editPrioritas = feladat.prioritas.toString()

                            editnevError = false
                            editkategoriaError = false
                            editprioritasError = false

                            szerkesztesDialog = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.editicon
                            ),
                            contentDescription = "Feladat szerkesztése"
                        )
                    }
                }
            }

            Button(
                onClick = {
                    onKesz()

                    Toast.makeText(
                        context,
                        if (feladat.kesz) {
                            "Feladat visszaállítva"
                        } else {
                            "Feladat elkészült"
                        },
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (feladat.kesz) {
                        "Visszaállítás aktívra"
                    } else {
                        "Megjelölés elkészültként"
                    }
                )
            }
        }
    }

    if (torlesDialog) {

        AlertDialog(
            onDismissRequest = {
                torlesDialog = false
            },
            title = {
                Text("Feladat törlése")
            },
            text = {
                Text("Biztosan törölni szeretnéd ezt a feladatot: ${feladat.nev}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        torlesDialog = false
                        onDelete()
                    }
                ) {
                    Text("Törlés")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        torlesDialog = false
                    }
                ) {
                    Text("Mégsem")
                }
            }
        )
    }

    if (szerkesztesDialog) {

        AlertDialog(
            onDismissRequest = {
                szerkesztesDialog = false
            },
            title = {
                Text("Feladat szerkesztése")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = editNev,
                        onValueChange = {
                            editNev = it
                            editnevError = false
                        },
                        label = { Text("Feladat neve") },
                        supportingText = {
                            if (editnevError) {
                                Text(
                                    "A név megadása kötelező!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editKategoria,
                        onValueChange = {
                            editKategoria = it
                            editkategoriaError = false
                        },
                        label = { Text("Kategória") },
                        supportingText = {
                            if (editkategoriaError) {
                                Text(
                                    "A kategória megadása kötelező!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = editPrioritas,
                        onValueChange = {
                            editPrioritas = it
                            editprioritasError = false
                        },
                        label = { Text("Prioritás (1-3)") },
                        supportingText = {
                            if (editprioritasError) {
                                Text(
                                    "Csak 1, 2 vagy 3 lehet!",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        editnevError = editNev.isBlank()
                        editkategoriaError = editKategoria.isBlank()

                        val ujPrioritas = editPrioritas.toIntOrNull()

                        editprioritasError =
                            ujPrioritas == null ||
                                    ujPrioritas < 1 ||
                                    ujPrioritas > 3

                        if (
                            editnevError ||
                            editkategoriaError ||
                            editprioritasError
                        ) {

                            Toast.makeText(
                                context,
                                "Ellenőrizd a megadott adatokat!",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            val modositottFeladat = Feladat(
                                id = feladat.id,
                                nev = editNev.trim(),
                                kategoria = editKategoria.trim(),
                                prioritas = ujPrioritas!!,
                                kesz = feladat.kesz
                            )

                            onEdit(modositottFeladat)
                            szerkesztesDialog = false

                            Toast.makeText(
                                context,
                                "Feladat módosítva!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text("Mentés")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        szerkesztesDialog = false
                    }
                ) {
                    Text("Mégsem")
                }
            }
        )
    }
}

@Composable
fun FeladatAvatar(nev: String) {

    val kezdobetu = nev.trim().firstOrNull()?.uppercase() ?: "?"

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = kezdobetu,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
 