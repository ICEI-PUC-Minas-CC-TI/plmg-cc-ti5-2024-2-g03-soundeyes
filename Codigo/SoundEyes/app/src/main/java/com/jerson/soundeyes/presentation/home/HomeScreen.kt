package com.jerson.soundeyes.presentation.home

import android.Manifest.permission
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jerson.soundeyes.consts.Const
import com.jerson.soundeyes.presentation.components.CardSelecao
import com.jerson.soundeyes.presentation.components.Dialog
import com.jerson.soundeyes.presentation.components.DropdownMenuMain
import com.jerson.soundeyes.presentation.components.NoPermissionContent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("InlinedApi")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
    event: (HomeEvent) -> Unit,
    navigationEvent: SharedFlow<NavigationEvent>,
    onNavigateCameraScreen: (String) -> Unit,
    onNavigateBluetoothCameraScreen: () -> Unit ,
    onNavigateAboutScreen:() -> Unit
) {

    val bluetoothPermissions = listOf(
        permission.BLUETOOTH,
        permission.BLUETOOTH_SCAN,
        permission.BLUETOOTH_CONNECT,
        permission.ACCESS_FINE_LOCATION,
        permission.WRITE_EXTERNAL_STORAGE,
        permission.CAMERA
    )
    val permissionState = rememberMultiplePermissionsState(bluetoothPermissions)
    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
        navigationEvent.collectLatest { navEvent ->
            when (navEvent) {
                NavigationEvent.ShowErrorDialog -> event(HomeEvent.ActivateDialogError)
                NavigationEvent.NavigateToBluetoothScreen -> onNavigateBluetoothCameraScreen()
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SoundEyes",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                   DropdownMenuMain(
                       onClickAbout = onNavigateAboutScreen
                   )
                }
            )
        }
    ) { paddingValues ->
        if (allPermissionsGranted(permissionState)){
            if(state.errorBluetooth){
                Dialog(
                    alert = "Ligue o Bluetooth para acessar a câmera via bluetooth!",
                    onDismiss = { event(HomeEvent.OnDismissBluetoothError) }
                )
            }
            if (state.errorDialog) {
                Dialog(
                    alert = "Erro de conexão com a câmera via Bluetooth!",
                    onDismiss = { event(HomeEvent.OnDismissDialogError) }
                )
            }

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CardSelecao(
                    tituloCard = "Resolução da imagem",
                    radioOptions = state.optionResolution,
                    selectedOption = state.selectResolution,
                    onOptionSelected = {
                        event(HomeEvent.OnSelectedResolution(it))
                    }
                )
                CardSelecao(
                    tituloCard = "Tipo de câmera",
                    radioOptions = state.optionTypeOfCamera,
                    selectedOption = state.selectCamera,
                    onOptionSelected = {
                        event(HomeEvent.OnSelectedCamera(it))
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        Log.d("options", state.selectCamera)
                        if (state.selectCamera == Const.CAMERA_DISPOSITIVO)
                            onNavigateCameraScreen(state.selectResolution)
                        else
                            event(HomeEvent.SendConfig)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(6.dp, RoundedCornerShape(10.dp)),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }else{
            Dialog(
                alert = "Aceite as permissões para ter conexão via bluetooth e utilizar a câmera!",
                onDismiss = {
                    permissionState.launchMultiplePermissionRequest()
                    event(HomeEvent.OnDismissDialogError)
                })
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .zIndex(1f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .size(50.dp)
                    .zIndex(2f)
            )
        }
    }
}
@OptIn(ExperimentalPermissionsApi::class)
fun allPermissionsGranted(permissionState: MultiplePermissionsState): Boolean {
    var isGranted:Boolean = false
    permissionState.permissions.forEach{ state ->
        isGranted = state.status.isGranted

    }
    return isGranted
}
