/*
* Universidad del Valle de Guatemala
* PROGRAMACIÓN DE APLICACIONES MÓVILES
* Sección: 20
* Autora: Tiffany Salazar Suarez
* Carnét: 24630
*/

package com.tiffany.salazar.laboratorio5

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.Alignment
import com.tiffany.salazar.laboratorio5.ui.theme.Laboratorio5Theme
import com.tiffany.salazar.laboratorio5.ImagenViewModel as ImagenViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laboratorio5Theme {
                ImagenList()
            }
        }
    }
}

data class Imagen(val id: String, val uri: Uri) //Representa cada imagen seleccionada por el usuario

class ImagenViewModel : ViewModel(){ //ViewModel para la lista de imágenes
    private val _imagenes = MutableStateFlow<List<Imagen>>(emptyList())
    val imagenes: StateFlow<List<Imagen>> = _imagenes.asStateFlow()

    fun addPhoto(uri: Uri){ //Función para agregar una nueva imagen a la lista
        viewModelScope.launch{
            val nuevaImagen = Imagen(
                id = System.currentTimeMillis().toString(),
                uri = uri
            )
            _imagenes.value += nuevaImagen
        }
    }
}

@Composable
fun ImagenList(viewModel: ImagenViewModel = viewModel()){ //Muestra la lista de imágenes y el botón flotante
    val imagenes by viewModel.imagenes.collectAsStateWithLifecycle()

    //Lanzador del Photo Picker
    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ){ uri: Uri? ->
        uri?.let { viewModel.addPhoto(it)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    pickMediaLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ){
                Icon(Icons.Filled.Add, contentDescription = "Agregar imagen")
            }
        }
    ){ innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize().padding(8.dp)
        ){
            if (imagenes.isEmpty()){
                Text(
                    text = "No has agregado imágenes aún, presiona el botón + para agregar una",
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }else{
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ){ items(
                        items = imagenes,
                        key = { imagen -> imagen.id }
                    ){ imagen ->
                        ImagenCard(imagen)
                    }
                }
            }
        }
    }
}

@Composable
fun ImagenCard(imagen: Imagen){ //Representa cada imagen dentro de una tarjeta y su identificador debajo
    Card(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imagen.uri).crossfade(true).build(),
                contentDescription = "Imagen seleccionada con ID ${imagen.id}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Foto ${imagen.id}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES) //Vista previa en modo oscuro
@Composable
fun ImagenListDarkPreview(){
    Laboratorio5Theme{
        ImagenList(viewModel = ImagenViewModel())
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO) //Vista previa en modo claro
@Composable
fun ImagenListLightPreview(){
    Laboratorio5Theme{
        ImagenList(viewModel = ImagenViewModel())
    }
}

