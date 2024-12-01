import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import android.os.Build

@Composable
fun PermissionsRequest(
    onPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current

    // Verificar se as permissões já foram concedidas
    var showDialog by remember { mutableStateOf(false) }
    var hasPermissions by remember {
        mutableStateOf(
            checkPermissions(context)
        )
    }

    // Launcher para solicitar múltiplas permissões
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasPermissions = permissions.values.all { it }
        if (hasPermissions) {
            onPermissionsGranted()
        }
    }

    // Exibir o diálogo se as permissões ainda não foram concedidas
    if (!hasPermissions && !showDialog) {
        showDialog = true
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.8f)
                    .height(200.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Permissão de câmera e Bluetooth necessárias.")
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = {
                        permissionsLauncher.launch(getPermissionsToRequest())
                        showDialog = false
                    }) {
                        Text("Solicitar permissões")
                    }
                }
            }
        }
    }
}

// Função para verificar se todas as permissões foram concedidas
private fun checkPermissions(context: Context): Boolean {
    val cameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    val bluetoothPermissions = listOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_SCAN else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else null,
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) Manifest.permission.ACCESS_FINE_LOCATION else null // Necessária para Bluetooth em versões anteriores a S
    ).all { permission ->
        permission == null || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    return cameraPermission && bluetoothPermissions
}

// Função para obter a lista de permissões a serem solicitadas
private fun getPermissionsToRequest(): Array<String> {
    val permissions = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION) // Necessária para Bluetooth em versões anteriores a S
    }
    return permissions.toTypedArray()
}
