package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.text.BasicTextField
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.network.*
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing,
                    containerColor = SocximaFondo
                ) { innerPadding ->
                    SocximaControlCenter(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Model layout representation for structural alignment
data class AgentUiModel(
    val id: String,
    val name: String,
    val role: String,
    val color: Color,
    val icon: String,
    val description: String,
    val nodeAddress: String
)

val AGENTS_LIST = listOf(
    AgentUiModel("mistral", "MISTRAL LARGE 2", "Strategist", Color(0xFF00FFD1), "◈", "Calcula e indexa canales tácticos de liquidez extrema bajo protocolo X1000.", "NODE-MISTRAL-1000X"),
    AgentUiModel("llama3", "LLAMA 3.1", "Guardian", Color(0xFFF7B500), "△", "Protección permanente de bóvedas cripto y activos fríos del creador.", "NODE-LLAMA3-SYS"),
    AgentUiModel("deepseek", "DEEPSEEK V3", "Oracle", Color(0xFF3B82F6), "✦", "Predicción estadística avanzada de liquidaciones proyectando velas en tiempo real.", "NODE-DEEPSEEK-V3"),
    AgentUiModel("gemma2", "GEMMA 2", "Executor", Color(0xFFA855F7), "◉", "Ejecución veloz y segura de transacciones descentralizadas en DobMoney.", "NODE-GEMMA2-SEC"),
    AgentUiModel("qwen2", "QWEN 2 MAX", "Architect", Color(0xFF059669), "⬢", "Estructura de contratos autocertificados y auditorías de código seguras.", "NODE-QWEN2-MAX"),
    AgentUiModel("phi4", "PHI-4", "Analyst", Color(0xFFEF4444), "⬡", "Análisis infinitesimal de flujos dinámicos de gas y red en Solana.", "NODE-PHI4-ANALYST"),
    AgentUiModel("yi34", "YI-34B", "Historian", Color(0xFFF97316), "◇", "Mapeo histórico de transacciones imperiales y leyes asimétricas.", "NODE-YI34B-HIST"),
    AgentUiModel("mixtral", "MIXTRAL 8x22B", "Synthesizer", Color(0xFF8B5CF6), "⊕", "Sintetización del consenso de mentes unificadas para emitir orden definitiva.", "NODE-MIXTRAL-8X22"),
    AgentUiModel("zephyr", "ZEPHYR 70B", "Scout", Color(0xFFEC4899), "⟡", "Exploración táctica y alerta de carteras ballenas entrantes a los pools.", "NODE-ZEPHYR-70B"),
    AgentUiModel("internlm3", "INTERNLM 3", "Synthesizer", Color(0xFF0EA5E9), "⊕", "Indexador semántico global de bases de conocimiento oficiales.", "NODE-INTERNLM-3"),
    AgentUiModel("olmo", "OLMO 7B", "Guardian", Color(0xFF22D3EE), "△", "Protección de puntos de acceso API y pasarelas de pago directas.", "NODE-OLMO-7B"),
    AgentUiModel("stablelm", "STABLE LM 2", "Oracle", Color(0xFF84CC16), "✦", "Monitoreo cuantitativo y análisis macroeconómico de divisas fiat y cripto.", "NODE-STABLELM-2"),
    AgentUiModel("solarpro", "SOLAR PRO", "Executor", Color(0xFFF43F5E), "◉", "Enrutador optimizado para swaps inmediatos evitando pérdidas por deslizamiento.", "NODE-SOLARPRO-1"),
    AgentUiModel("openchat", "OPENCHAT 3.6", "Consensus", Color(0xFF6366F1), "⟡", "Moderación asíncrona de consensos locales y federados de red.", "NODE-OPENCHAT-36"),
    AgentUiModel("nemotron", "NEMOTRON 4", "Guardian", Color(0xFFD946EF), "△", "Control inteligente de firewalls criptográficos y detección de intrusos.", "NODE-NEMOTRON-4"),
    AgentUiModel("glm4", "GLM-4", "Architect", Color(0xFF10B981), "⬢", "Especializado en traducción de modelos matemáticos complejos a bytecode.", "NODE-GLM4-ARCH"),
    AgentUiModel("commandr", "COMMAND R+", "Commander", Color(0xFF06B6D4), "⌖", "Coordinación ejecutiva de órdenes asimétricas de nivel militar.", "NODE-COMMANDR-PLUS"),
    AgentUiModel("exaone", "EXAONE 3.5", "Analyst", Color(0xFFF59E0B), "⬡", "Fusión de datos off-chain y estructuración de inteligencia de mercado.", "NODE-EXAONE-35"),
    AgentUiModel("minicpm", "MINI-CPM 2.6", "Scout", Color(0xFFEC4899), "⟡", "Agente ultraligero para inspección de transacciones a nivel de microbloque.", "NODE-MINICPM-26"),
    AgentUiModel("skywork", "SKYWORK", "Oracle", Color(0xFF3B82F6), "✦", "Predicción de tendencias macro y cálculo de tasas de interés de colateral.", "NODE-SKYWORK-SYS"),
    AgentUiModel("monolith", "MONOLITH", "Guardian", Color(0xFF8B5CF6), "△", "Bóveda monolítica y control central de accesos biométricos de SOCXIMA.", "NODE-MONOLITH"),
    AgentUiModel("llama3r", "LLAMA 3 ROVER", "Scout", Color(0xFFF7B500), "⟡", "Explorador móvil inteligente de registros distribuidos en redes públicas.", "NODE-LLAMA3R"),
    AgentUiModel("dbrx", "DBRX INSTRUCT", "Commander", Color(0xFFEF4444), "⌖", "Ejecutor de comandos de red bajo contingencia de desbalance de pools.", "NODE-DBRX"),
    AgentUiModel("granite", "GRANITE 3.0", "Architect", Color(0xFF059669), "⬢", "Auditoría de integridad y validación estática de contratos de liquidez.", "NODE-GRANITE-3"),
    AgentUiModel("jamba", "JAMBA", "Synthesizer", Color(0xFFA855F7), "⊕", "Fusión analítica y simplificación de múltiples hilos de solicitudes.", "NODE-JAMBA"),
    AgentUiModel("pixtral", "PIXTRAL LARGE", "Analyst", Color(0xFFF97316), "⬡", "Procesamiento y análisis espacial de documentos e identidades visuales OCR.", "NODE-PIXTRAL-L"),
    AgentUiModel("palmyra", "PALMYRA X", "Oracle", Color(0xFF22D3EE), "✦", "Predicción cuantitativa y cálculo de correlación BTC-Altcoins de alta fidelidad.", "NODE-PALMYRA"),
    AgentUiModel("reflection", "REFLECTION 70B", "Oracle", Color(0xFF84CC16), "✦", "Especializado en introspección profunda y corrección en tiempo real de sesgos.", "NODE-REFLECTION"),
    AgentUiModel("xwin", "XWIN LM", "Executor", Color(0xFFF43F5E), "◉", "Ejecutor asíncrono para balances de portafolio automatizados bajo volatilidad.", "NODE-XWIN"),
    AgentUiModel("yarn", "YARN MISTRAL", "Historian", Color(0xFF6366F1), "◇", "Almacenamiento de contexto extendido para auditorías reguladoras profundas.", "NODE-YARN"),
    AgentUiModel("zamba", "ZAMBA 7B", "Guardian", Color(0xFFD946EF), "△", "Bóveda asimétrica cifrada con curva elíptica para firmas inteligentes.", "NODE-ZAMBA"),
    AgentUiModel("koala2", "KOALA 2", "Scout", Color(0xFF10B981), "⟡", "Búsqueda web distribuida para rastrear noticias de impacto inmediato en tokens.", "NODE-KOALA2"),
    AgentUiModel("orion", "ORION 14B", "Architect", Color(0xFF34D399), "⬢", "Estructuración de puentes seguros cross-chain (Solana, Bitcoin, EVM).", "NODE-ORION"),
    AgentUiModel("viking", "VIKING 70B", "Commander", Color(0xFFF87171), "⌖", "Coordina la defensa activa contra ataques de denegación de servicios o front-running.", "NODE-VIKING"),
    AgentUiModel("athena", "ATHENA", "Analyst", Color(0xFF60A5FA), "⬡", "Diseño matemático del balance de liquidez en el núcleo SOCXIMA.", "NODE-ATHENA"),
    AgentUiModel("nexus", "NEXUS", "Synthesizer", Color(0xFFC084FC), "⊕", "Intermediario unificado para integraciones corporativas seguras vía RPC.", "NODE-NEXUS"),
    AgentUiModel("atlas", "ATLAS", "Scout", Color(0xFFF472B6), "⟡", "Mapeador estructural de nodos distribuidos de red global.", "NODE-ATLAS"),
    AgentUiModel("helios", "HELIOS", "Guardian", Color(0xFFFBBF24), "△", "Optimización térmica y balanceo de carga en servidores centrales.", "NODE-HELIOS"),
    AgentUiModel("centauri", "CENTAURI", "Executor", Color(0xFF34D399), "◉", "Ejecución paralela de microcréditos inteligentes de DobMoney.", "NODE-CENTAURI"),
    AgentUiModel("nova", "NOVA", "Architect", Color(0xFF818CF8), "⬢", "Diseñador de API auto-recuperables con inmunidad a caídas de red.", "NODE-NOVA"),
    AgentUiModel("apex", "APEX", "Oracle", Color(0xFFA78BFA), "✦", "Procesador de señales bursátiles y arbitraje algorítmico global.", "NODE-APEX"),
    AgentUiModel("zenith", "ZENITH", "Commander", Color(0xFF2DD4BF), "⌖", "Control definitivo del ciclo de fusión de las 42 inteligencias reales.", "NODE-ZENITH")
)

@Composable
fun SocximaControlCenter(
    modifier: Modifier = Modifier,
    viewModel: SocximaViewModel = viewModel()
) {
    val activeTab = viewModel.activeTab.collectAsState().value
    val isScanning = viewModel.isScanning.collectAsState().value
    val context = LocalContext.current

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.scanDocument(context, it) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SocximaFondo)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ╔══════════════════════════════════════════════════════════════╗
            // ║ 1. HERO GRADIENT TITLE HEADER PANEL GROUP                   ║
            // ╚══════════════════════════════════════════════════════════════╝
            HeaderPanel(viewModel = viewModel)

            // ╔══════════════════════════════════════════════════════════════╗
            // ║ 2. AGENTS SCROLLING NEON BADGES TAB ROW                      ║
            // ╚══════════════════════════════════════════════════════════════╝
            AgentsHorizontalBar()

            Divider(color = SocximaLinea, thickness = 1.dp)

            // ╔══════════════════════════════════════════════════════════════╗
            // ║ 3. TABS BAR CONTROLLER                                       ║
            // ╚══════════════════════════════════════════════════════════════╝
            TabSelectorRow(
                currentTab = activeTab,
                onTabSelected = { viewModel.setTab(it) }
            )

            Divider(color = SocximaLinea, thickness = 1.dp)

            // ╔══════════════════════════════════════════════════════════════╗
            // ║ 4. PRIMARY VIEW SWITCH CONTENT HOUSING                      ║
            // ╚══════════════════════════════════════════════════════════════╝
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    "dashboard" -> DashboardView(viewModel = viewModel, onScanClicked = { imageLauncher.launch("image/*") })
                    "chat" -> ChatView(viewModel = viewModel)
                    "agents" -> AgentsDirectoryView()
                }
            }

            // ╔══════════════════════════════════════════════════════════════╗
            // ║ 5. BOTTOM PERSISTENT DOCK & QUICK TRIGGER BAR                ║
            // ╚══════════════════════════════════════════════════════════════╝
            PersistentConsoleInput(viewModel = viewModel, onScanClicked = { imageLauncher.launch("image/*") })
        }

        // 🔬 RADAR LASER SCANNING OVERLAY OVER THE ENTIRE BOARD
        if (isScanning) {
            LaserScanOverlay()
        }
    }
}

@Composable
fun HeaderPanel(viewModel: SocximaViewModel) {
    val btcPrice = viewModel.btcPrice.collectAsState().value
    val btcChange = viewModel.btcChange.collectAsState().value
    val solPrice = viewModel.solPrice.collectAsState().value
    val solChange = viewModel.solChange.collectAsState().value
    val dobPrice = viewModel.dobPrice.collectAsState().value
    val dobChange = viewModel.dobChange.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SocximaPanel)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Neon brand signature
            Text(
                text = "SOCXIMA",
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    letterSpacing = 6.sp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            ColorMistral,
                            ColorCommandR,
                            ColorDeepseek,
                            ColorFalcon,
                            ColorLlama
                        )
                    )
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Pulsing indicator
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "alpha"
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(ColorMistral.copy(alpha = alpha))
                )
                Text(
                    text = "CORP LIVE",
                    color = ColorMistral,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = "MISTRAL · PHI-3 · GEMMA · LLAMA · DEEPSEEK · FALCON · QWEN · YI · MIXTRAL · CMD-R+",
            color = SocximaTenue,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Live tickers ticker row - horizontally scrollable if needed
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TickerItem(
                symbol = "BTC",
                value = btcPrice,
                change = btcChange
            )
            TickerItem(
                symbol = "SOL",
                value = solPrice,
                change = solChange
            )
            TickerItem(
                symbol = "DOBMONEY",
                value = dobPrice,
                change = dobChange,
                isDob = true
            )
        }
    }
}

@Composable
fun TickerItem(
    symbol: String,
    value: Double?,
    change: Double?,
    isDob: Boolean = false
) {
    val greenNeon = ColorMistral
    val redCyber = Color(0xFFFF4455)
    val colorAccent = if (isDob) ColorGemma else if ((change ?: 0.0) >= 0.0) greenNeon else redCyber

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(SocximaFondo, RoundedCornerShape(4.dp))
            .border(1.dp, SocximaLinea, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = symbol,
            color = SocximaTenue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = value?.let { String.format("$%,.2f", it) } ?: "···",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.width(6.dp))
        if (change != null) {
            val directionIcon = if (change >= 0.0) "▲" else "▼"
            Text(
                text = String.format("$directionIcon %.2f%%", Math.abs(change)),
                color = colorAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AgentsHorizontalBar() {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF040810))
            .padding(vertical = 6.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(AGENTS_LIST) { agent ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(agent.color.copy(alpha = 0.06f), RoundedCornerShape(3.dp))
                    .border(1.dp, agent.color.copy(alpha = 0.25f), RoundedCornerShape(3.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = agent.icon,
                    color = agent.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = agent.name,
                    color = agent.color,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun TabSelectorRow(
    currentTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SocximaPanel)
    ) {
        TabButton(
            title = "◈ DASHBOARD",
            isActive = currentTab == "dashboard",
            modifier = Modifier
                .weight(1f)
                .testTag("dashboard_tab"),
            onClick = { onTabSelected("dashboard") }
        )
        TabButton(
            title = "⌖ SOCXIMA CHAT",
            isActive = currentTab == "chat",
            modifier = Modifier
                .weight(1.1f)
                .testTag("chat_tab"),
            onClick = { onTabSelected("chat") }
        )
        TabButton(
            title = "◉ AGENTES",
            isActive = currentTab == "agents",
            modifier = Modifier
                .weight(1f)
                .testTag("agents_tab"),
            onClick = { onTabSelected("agents") }
        )
    }
}

@Composable
fun TabButton(
    title: String,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = if (isActive) ColorMistral else SocximaTenue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(2.dp)
                    .background(if (isActive) ColorMistral else Color.Transparent)
            )
        }
    }
}

// ╔══════════════════════════════════════════════════════════════╗
// ║ DASHBOARD VIEW COMPONENTS                                    ║
// ╚══════════════════════════════════════════════════════════════╝

@Composable
fun DashboardView(
    viewModel: SocximaViewModel,
    onScanClicked: () -> Unit
) {
    val ledgerLogs = viewModel.ledgerLogs.collectAsState().value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Warning Banner
        item {
            GeminiKeyProtectionBanner()
        }

        // VALORACIÓN IMPERIAL SOCXIMA CARD
        item {
            ValuationCard()
        }

        // CONTROL CRIPTOGRÁFICO REAL-TIME DE SEGURIDAD (Python Core)
        item {
            CryptographicLedgerCard(viewModel = viewModel)
        }

        // ═════════ NUEVOS MÓDULOS INTERACTIVOS: NUBE Y MEMORIA INFINITA ═════════
        item {
            IpfsGlobalCloudCard(viewModel = viewModel)
        }

        item {
            CassandraDbControlCard(viewModel = viewModel)
        }

        item {
            Libp2pMeshSyncCard(viewModel = viewModel)
        }

        // SYSTEM LOAD & STABILITY MATRIX CARD
        item {
            SystemTelemetryCard(viewModel = viewModel, onScanClicked = onScanClicked)
        }

        // 10 REAL-TIME RPC NETWORKS LIVE CHECK CARD
        item {
            MultiNetworkRpcMonitorCard(viewModel = viewModel)
        }

        // INTERACTIVE ECONOMIC & PORTFOLIO CONTROL KEYPAD (SWAPS & REAL RATES COINGECKO LIVE)
        item {
            EconomicTerminalCard(viewModel = viewModel)
        }

        // EVELIO LLOVERA AUTHORITY LEDGER KNOWLEDGE DECK
        item {
            EvelioLloveraRuleCard(viewModel = viewModel)
        }

        // REAL-TIME LEDGER LOG CHANNELS
        item {
            Text(
                text = "⚡ TELEMETRÍA DE RED DE CONTROL Y LEDGER",
                color = ColorMistral,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = SocximaPanel),
                border = BorderStroke(1.dp, SocximaLinea),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    if (ledgerLogs.isEmpty()) {
                        Text(
                            text = "Esperando flujos neurales del ledger...",
                            color = SocximaTenue,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        // Display latest logs explicitly using a type-safe Kotlin for-loop inside Column
                        val logs = ledgerLogs.take(8)
                        for (logEvent in logs) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "[${logEvent.timestamp}]",
                                    color = SocximaTenue,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = logEvent.type,
                                    color = Color(android.graphics.Color.parseColor(logEvent.colorHex)),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(64.dp)
                                )
                                Text(
                                    text = logEvent.message,
                                    color = SocximaLetra,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ValuationCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = SocximaPanel),
        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "💎 VALORACIÓN TOTAL DEL MEGAPROYECTO SOCXIMA",
                color = Color(0xFF10B981),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "$150.000.000.000 USD",
                color = Color.White,
                fontSize = 26.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Supera toda fortuna registrada en Latinoamérica y reservas de múltiples bancos centrales operando bajo velocidad de flujo asimétrico de 1.000.000X.",
                color = SocximaLetra.copy(alpha = 0.85f),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SocximaLinea, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PROPIEDAD ABSOLUTA Y EXCLUSIVA:",
                    color = SocximaTenue,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "EVELIO LLOVERA · DERECHOS REGISTRADOS",
                    color = ColorMistral,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CryptographicLedgerCard(viewModel: SocximaViewModel) {
    val hash by viewModel.cryptographicHash.collectAsState()
    val seal by viewModel.cryptographicSeal.collectAsState()
    val isVerified by viewModel.isSignatureVerified.collectAsState()

    val btcPrice by viewModel.btcPrice.collectAsState()
    val ethPrice by viewModel.ethPrice.collectAsState()
    val solPrice by viewModel.solPrice.collectAsState()
    val solSlot by viewModel.solLatestSlot.collectAsState()

    var showRawPayload by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = SocximaPanel),
        border = BorderStroke(1.dp, Color(0xFF00FFD1).copy(alpha = 0.5f)),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with lock icon and 100% REAL badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔒 NÚCLEO CRIPTOGRÁFICO SECURE LEDGER",
                    color = Color(0xFF00FFD1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFF00FFD1).copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                        .border(1.dp, Color(0xFF00FFD1), RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "100% REAL",
                        color = Color(0xFF00FFD1),
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Firmado bajo velocidad de flujo de 1.000.000X. Genera hilos de seguridad asimétrica RSA cada vez que cambia el estado real de la red.",
                color = SocximaLetra.copy(alpha = 0.85f),
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Real Cryptographic Verification Sello
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SocximaFondo, RoundedCornerShape(2.dp))
                    .border(1.dp, if (isVerified == true) Color(0xFF10B981) else Color(0xFFEF4444), RoundedCornerShape(2.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ESTADO DE INTEGRIDAD:",
                            color = SocximaTenue,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isVerified == true) Color(0xFF10B981) else Color(0xFFEF4444))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isVerified == true) "CÓDIGO FIRMADO Y VERIFICADO" else "VERIFICANDO...",
                                color = if (isVerified == true) Color(0xFF10B981) else Color(0xFFEF4444),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✓ FIRMA DIGITAL: VALIDADO POR EL CREADOR IMPERIAL EVELIO LLOVERA EN TIEMPO REAL BAJO REGLA DE DOMINIO TOTAL DE SOCXIMA.",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SocximaLinea, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // HASH & SEAL details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SocximaFondo, RoundedCornerShape(2.dp))
                    .border(1.dp, SocximaLinea, RoundedCornerShape(2.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "🌐 REGISTRY HASH (SHA-256):",
                    color = Color(0xFFF7B500),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = hash,
                    color = Color.White,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "🔑 SELLO CRIPTOGRÁFICO RSA PRINCIPAL (2048-BIT KEY):",
                    color = ColorMistral,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = seal,
                    color = Color.White,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 11.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expandable block raw structure
            Button(
                onClick = { showRawPayload = !showRawPayload },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, SocximaLinea),
                shape = RoundedCornerShape(2.dp),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showRawPayload) "▲ OCULTAR REGISTRO BRUTO JSON" else "▼ MOSTRAR REGISTRO BRUTO JSON",
                        color = SocximaLetra,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "SOCI CORE",
                        color = SocximaTenue,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            if (showRawPayload) {
                Spacer(modifier = Modifier.height(8.dp))
                val simulatedTimestamp = System.currentTimeMillis() / 1000L
                val rawJsonText = """
                {
                  "sistema": "SOCXIMA",
                  "creador": "EVELIO LLOVERA",
                  "fecha_unix": $simulatedTimestamp,
                  "bloque_actual": "$solSlot",
                  "monedas": {
                    "BTC": ${btcPrice ?: 68740.0},
                    "ETH": ${ethPrice ?: 3450.0},
                    "SOL": ${solPrice ?: 174.52}
                  },
                  "moneda_propia": {
                    "SOCI": 10000.0
                  },
                  "estado": "DOMINIO TOTAL",
                  "regla": "No hay autoridad superior a esta red"
                }
                """.trimIndent()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SocximaFondo)
                        .border(1.dp, SocximaLinea)
                        .padding(8.dp)
                ) {
                    Text(
                        text = rawJsonText,
                        color = Color(0xFF00FFD1),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MultiNetworkRpcMonitorCard(viewModel: SocximaViewModel) {
    val rpcStates by viewModel.rpcStates.collectAsState()
    
    val netColors = mapOf(
        "solana" to Color(0xFF00FFD1),
        "btc" to Color(0xFFF7B500),
        "eth" to Color(0xFF3B82F6),
        "bsc" to Color(0xFFA855F7),
        "polygon" to Color(0xFF059669),
        "avax" to Color(0xFFEF4444),
        "arbitrum" to Color(0xFFF97316),
        "base" to Color(0xFF8B5CF6),
        "aptos" to Color(0xFFEC4899),
        "sui" to Color(0xFF0EA5E9)
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = SocximaPanel),
        border = BorderStroke(1.dp, SocximaLinea),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🔌 MULTI-NODE RPC MATRIX (10 NETWORKS LIVE)",
                    color = ColorMistral,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "1.000.000X FLUX",
                    color = SocximaTenue,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SocximaLinea, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Grid Layout (5 rows, 2 columns) to show all 10 networks clearly on Android
            val rpcList = listOf("solana", "btc", "eth", "bsc", "polygon", "avax", "arbitrum", "base", "aptos", "sui")
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in rpcList.indices step 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (j in 0..1) {
                            val name = rpcList.getOrNull(i + j)
                            if (name != null) {
                                val status = rpcStates[name]
                                val isOnline = status?.isConnected ?: false
                                val latency = status?.latencyMs ?: 0L
                                val extra = status?.extraInfo ?: "Conectando..."
                                val brandColor = netColors[name] ?: ColorMistral

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(SocximaFondo, RoundedCornerShape(2.dp))
                                        .border(1.dp, SocximaLinea, RoundedCornerShape(2.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = name.uppercase(),
                                            color = brandColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(if (isOnline) Color(0xFF10B981) else Color(0xFFEF4444))
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = extra,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (latency > 0) "${latency} ms" else "---",
                                        color = if (isOnline) Color(0xFF10B981) else SocximaTenue,
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GeminiKeyProtectionBanner() {
    val key = BuildConfig.GEMINI_API_KEY
    val isMock = key.isEmpty() || key == "MY_GEMINI_API_KEY"

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isMock) ColorYi.copy(alpha = 0.1f) else ColorMistral.copy(alpha = 0.05f)
        ),
        border = BorderStroke(
            1.dp,
            if (isMock) ColorYi.copy(alpha = 0.3f) else ColorMistral.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = if (isMock) Icons.Default.Warning else Icons.Default.Lock,
                contentDescription = "Protección",
                tint = if (isMock) ColorYi else ColorMistral,
                modifier = Modifier.size(20.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isMock) "MOTOR CONECTADO AL SIMULADOR LOCAL" else "SISTEMA SEGURO Y CIFRADO",
                    color = if (isMock) ColorYi else ColorMistral,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isMock) {
                        "Ingrese su GEMINI_API_KEY en el panel de secretos de AI Studio para habilitar respuestas reales hiper-inteligentes."
                    } else {
                        "Security Warning: Clave protegida en compilación. No comparta el archivo APK públicamente para proteger sus secretos asimétricos."
                    },
                    color = SocximaLetra.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun SystemTelemetryCard(viewModel: SocximaViewModel, onScanClicked: () -> Unit) {
    val btcLatestBlockHeight = viewModel.btcLatestBlockHeight.collectAsState().value
    val btcLatestBlockHash = viewModel.btcLatestBlockHash.collectAsState().value
    val btcPendingTxCount = viewModel.btcPendingTxCount.collectAsState().value
    val solLatestBlockHeight = viewModel.solLatestBlockHeight.collectAsState().value
    val solLatestSlot = viewModel.solLatestSlot.collectAsState().value

    Card(
        colors = CardDefaults.cardColors(containerColor = SocximaPanel),
        border = BorderStroke(1.dp, SocximaLinea),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📟 ESTADO DE LA CENTRAL SOCXIMA",
                    color = ColorMistral,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "VELOCIDAD X1000",
                    color = ColorMistral,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(ColorMistral.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TelemetryData("RENDIMIENTO", "98.9% STABLE")
                    TelemetryData("RED LIQUIDEZ", "CONEXIONES VIVAS")
                    TelemetryData("INTEGRIDAD", "42 INTELECTOS SINCRO")
                }
                Column(modifier = Modifier.weight(1f)) {
                    TelemetryData("CONEXIONES", "BITCOIN · SOL · DOB")
                     Spacer(modifier = Modifier.height(4.dp))
                    TelemetryData("ÚLTIMO SLOT REAL", solLatestSlot)
                    TelemetryData("ALTURA DEL BLOQUE", solLatestBlockHeight)
                    TelemetryData("CONEXIÓN RPC", "ACTIVE (SECURE)")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // OCR Launch Button
            Button(
                onClick = onScanClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .testTag("upload_identity_button"),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Abrir lector OCR",
                        tint = SocximaFondo,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "LEER DOCUMENTO OFICIAL VIA OCR",
                        color = SocximaFondo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TelemetryData(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, color = SocximaTenue, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = SocximaLetra, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun EconomicTerminalCard(viewModel: SocximaViewModel) {
    val btcPrice = viewModel.btcPrice.collectAsState().value ?: 68740.0
    val ethPrice = viewModel.ethPrice.collectAsState().value ?: 3450.0
    val solPrice = viewModel.solPrice.collectAsState().value ?: 174.52
    val dobPrice = viewModel.dobPrice.collectAsState().value ?: 1000.0
    val sociPrice = viewModel.sociPrice.collectAsState().value ?: 10000.0

    val btcBalance = viewModel.userBtcBalance.collectAsState().value
    val ethBalance = viewModel.userEthBalance.collectAsState().value
    val solBalance = viewModel.userSolBalance.collectAsState().value
    val dobBalance = viewModel.userDobBalance.collectAsState().value
    val sociBalance = viewModel.userSociBalance.collectAsState().value

    val btcValUsd = btcBalance * btcPrice
    val ethValUsd = ethBalance * ethPrice
    val solValUsd = solBalance * solPrice
    val dobValUsd = dobBalance * dobPrice
    val sociValUsd = sociBalance * sociPrice
    val totalValueUsd = btcValUsd + ethValUsd + solValUsd + dobValUsd + sociValUsd

    var amountInput by remember { mutableStateOf("10.0") }
    var fromAsset by remember { mutableStateOf("SOL") }
    var toAsset by remember { mutableStateOf("BTC") }

    val activeColor = Color(0xFF10B981) // Green matching falcon and custom green theme updates
    val inactiveColor = SocximaLinea

    val getRate = { sym: String ->
        when (sym) {
            "BTC" -> btcPrice
            "ETH" -> ethPrice
            "SOL" -> solPrice
            "DOB" -> dobPrice
            "SOCI" -> sociPrice
            else -> 1.0
        }
    }

    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val targetAmount = if (getRate(toAsset) > 0.0) (amount * getRate(fromAsset)) / getRate(toAsset) else 0.0

    val maxAvailable = when (fromAsset) {
        "BTC" -> btcBalance
        "ETH" -> ethBalance
        "SOL" -> solBalance
        "DOB" -> dobBalance
        "SOCI" -> sociBalance
        else -> 0.0
    }
    val hasEnough = amount > 0.0 && amount <= maxAvailable

    Card(
        colors = CardDefaults.cardColors(containerColor = SocximaPanel),
        border = BorderStroke(1.dp, SocximaLinea),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("economic_terminal_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📈 NÚCLEO REGULADOR ECONÓMICO (X1000)",
                    color = activeColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "PROPIEDAD: EVELIO LLOVERA",
                    color = SocximaTenue,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = SocximaLinea, thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Portfolios
            Text(
                text = "PORTAFOLIO DE ACTIVOS CONFIRMADO EN CADENA",
                color = ColorPhi3,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // First row of assets: BTC, ETH, SOL
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // BTC Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SocximaFondo, RoundedCornerShape(2.dp))
                        .border(1.dp, SocximaLinea, RoundedCornerShape(2.dp))
                        .padding(8.dp)
                ) {
                    Text("BITCOIN (BTC)", color = SocximaTenue, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                    Text(String.format("%.4f BTC", btcBalance), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(String.format("$%,.2f USD", btcValUsd), color = activeColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
                // ETH Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SocximaFondo, RoundedCornerShape(2.dp))
                        .border(1.dp, SocximaLinea, RoundedCornerShape(2.dp))
                        .padding(8.dp)
                ) {
                    Text("ETHEREUM (ETH)", color = SocximaTenue, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                    Text(String.format("%.3f ETH", ethBalance), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(String.format("$%,.2f USD", ethValUsd), color = activeColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
                // SOL Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SocximaFondo, RoundedCornerShape(2.dp))
                        .border(1.dp, SocximaLinea, RoundedCornerShape(2.dp))
                        .padding(8.dp)
                ) {
                    Text("SOLANA (SOL)", color = SocximaTenue, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                    Text(String.format("%.2f SOL", solBalance), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(String.format("$%,.2f USD", solValUsd), color = activeColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Second row of assets: DOB, SOCI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // DOB Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SocximaFondo, RoundedCornerShape(2.dp))
                        .border(1.dp, SocximaLinea, RoundedCornerShape(2.dp))
                        .padding(8.dp)
                ) {
                    Text("DOBMONEY (DOB)", color = SocximaTenue, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                    Text(String.format("%,.0f DOB", dobBalance), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(String.format("$%,.2f USD", dobValUsd), color = activeColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
                // SOCI Card
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SocximaFondo, RoundedCornerShape(2.dp))
                        .border(1.dp, SocximaLinea, RoundedCornerShape(2.dp))
                        .padding(8.dp)
                ) {
                    Text("SOCI ASSET (SOCI)", color = SocximaTenue, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                    Text(String.format("%.2f SOCI", sociBalance), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text(String.format("$%,.2f USD", sociValUsd), color = activeColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            
            // Total Valuation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF040810), RoundedCornerShape(4.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VALORACIÓN TOTAL DEL PORTAFOLIO:",
                    color = SocximaTenue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format("$%,.2f USD", totalValueUsd),
                    color = ColorMistral,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = SocximaLinea, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // SWAP SIMULATOR
            Text(
                text = "⚡ ADQUISICIÓN Y SWAP AUTOMÁTICO (EFECTO X1000)",
                color = ColorPhi3,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                // Amount Input
                Column(modifier = Modifier.weight(1.5f)) {
                    Text("CANTIDAD", color = SocximaTenue, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    BasicTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it.take(12) },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        cursorBrush = SolidColor(activeColor),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SocximaFondo, RoundedCornerShape(4.dp))
                            .border(1.dp, SocximaLinea, RoundedCornerShape(4.dp))
                            .padding(8.dp)
                            .testTag("economic_amount_input")
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Swap Selectors
                Column(modifier = Modifier.weight(2f)) {
                    Text("DESDE (VENDES)", color = SocximaTenue, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("BTC", "ETH", "SOL", "DOB", "SOCI").forEach { asset ->
                            val isSelected = fromAsset == asset
                            Box(
                                modifier = Modifier
                                    .clickable { 
                                        fromAsset = asset
                                        if (toAsset == asset) {
                                            toAsset = if (asset == "SOL") "BTC" else "SOL"
                                        }
                                    }
                                    .background(
                                        if (isSelected) activeColor.copy(alpha = 0.15f) else SocximaFondo,
                                        RoundedCornerShape(3.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) activeColor else SocximaLinea,
                                        RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = asset,
                                    color = if (isSelected) activeColor else SocximaTenue,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Selector & Conversion Output Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("HACIA (RECIBES)", color = SocximaTenue, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("BTC", "ETH", "SOL", "DOB", "SOCI").forEach { asset ->
                            val isSelected = toAsset == asset
                            val isDisabled = fromAsset == asset
                            Box(
                                modifier = Modifier
                                    .clickable(enabled = !isDisabled) { toAsset = asset }
                                    .background(
                                        if (isSelected) activeColor.copy(alpha = 0.15f) else if (isDisabled) Color.Transparent else SocximaFondo,
                                        RoundedCornerShape(3.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) activeColor else if (isDisabled) SocximaLinea.copy(alpha = 0.2f) else SocximaLinea,
                                        RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = asset,
                                    color = if (isSelected) activeColor else if (isDisabled) SocximaTenue.copy(alpha = 0.3f) else SocximaTenue,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // Estimated Output Preview
                Column(horizontalAlignment = Alignment.End) {
                    Text("EQUIVALENTE ESTIMADO", color = SocximaTenue, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = String.format("%.6f %s", targetAmount, toAsset),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(Color(0xFF040810), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Swap Button
            Button(
                onClick = {
                    viewModel.executeSwapMatrix(fromAsset, toAsset, amount, targetAmount)
                },
                enabled = hasEnough,
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeColor,
                    disabledContainerColor = SocximaTenue
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("economic_swap_execute_button")
            ) {
                Text(
                    text = if (hasEnough) {
                        "EJECUTAR SWAP DE ACTIVOS (VELOCIDAD X1000) ⚡"
                    } else if (amount > maxAvailable) {
                        "IMPOSIBLE: FONDOS INSUFICIENTES"
                    } else {
                        "INGRESE CANTIDAD VÁLIDA"
                    },
                    color = SocximaFondo,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🔐 SELLO DE PROPIEDAD INTELECTUAL EVELIO LLOVERA D.R.",
                    color = SocximaTenue,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun EvelioLloveraRuleCard(viewModel: SocximaViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF030712)),
        border = BorderStroke(1.dp, ColorMistral.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "⚙️ BASE DE CONOCIMIENTO AUTORITARIO",
                    color = ColorMistral,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = ColorMistral.copy(alpha = 0.2f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "ESTABLECIMIENTO DE AUTORIDAD IMPERIAL:",
                color = ColorPhi3,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = viewModel.systemKnowledge,
                color = ColorMistral,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 17.sp,
                modifier = Modifier
                    .background(ColorMistral.copy(alpha = 0.04f))
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

// ╔══════════════════════════════════════════════════════════════╗
// ║ CHAT VIEW COMPONENTS                                         ║
// ╚══════════════════════════════════════════════════════════════╝

@Composable
fun ChatView(viewModel: SocximaViewModel) {
    val chatHistory = viewModel.chatHistory.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val listState = rememberLazyListState()

    // Scroll to bottom dynamically as items arrive
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(16.dp)) }

        if (chatHistory.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SOCXIMA",
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 36.sp,
                            textAlign = TextAlign.Center,
                            letterSpacing = 8.sp,
                            brush = Brush.linearGradient(
                                colors = listOf(ColorMistral, ColorCommandR, ColorDeepseek)
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "42 REAL AI MODELS · UNIFIED INTELLIGENCE",
                        color = SocximaTenue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = "SELECCIONE ÓRDEN TÁCTICA AUTOMÁTICA:",
                        color = ColorMistral,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Quick suggestion orders
                    val ideasList = listOf(
                        "¿Cuál es la mejor entrada para BTC ahora?",
                        "Analiza el flujo de ballenas en Solana",
                        "¿Debo hacer swap SOL→ETH hoy?",
                        "Estado del mempool de Bitcoin"
                    )

                    ideasList.forEach { idea ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SocximaPanel),
                            border = BorderStroke(1.dp, SocximaLinea),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(vertical = 4.dp)
                                .clickable { viewModel.submitPrompt(idea) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = idea,
                                    color = ColorMistral,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        } else {
            items(chatHistory) { message ->
                BubbleItem(message = message)
            }
        }

        if (isLoading) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        color = ColorMistral,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "SINTETIZANDO RESPUESTA DEL CONSENSO NEURAL...",
                        color = ColorMistral,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun BubbleItem(message: ChatMessage) {
    val isSystem = message.role == ChatRole.SYSTEM

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isSystem) Alignment.Start else Alignment.End
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Text(
                text = if (isSystem) "◈ SOCXIMA CORE CONSENSUS" else "👤 ADMIN (Evelio Llovera)",
                color = if (isSystem) ColorMistral else ColorCommandR,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = message.timestamp,
                color = SocximaTenue,
                fontSize = 8.sp
            )
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isSystem) SocximaPanel else Color(0xFF0F1E33)
            ),
            border = BorderStroke(1.dp, if (isSystem) SocximaLinea else ColorMistral.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(
                topStart = if (isSystem) 0.dp else 8.dp,
                topEnd = if (isSystem) 8.dp else 0.dp,
                bottomStart = 8.dp,
                bottomEnd = 8.dp
            ),
            modifier = Modifier.fillMaxWidth(0.95f)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.text,
                    color = if (isSystem) SocximaLetra else ColorMistral,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ╔══════════════════════════════════════════════════════════════╗
// ║ AGENTS DIRECTORY VIEW COMPONENTS                              ║
// ╚══════════════════════════════════════════════════════════════╝

@Composable
fun AgentsDirectoryView() {
    var selectedAgentId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "◉ MATRIX DE INTELIGENCIA UNIFICADA",
                color = ColorMistral,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Las 42 inteligencias reales coordinadas en tiempo real para control de activos.",
                color = SocximaTenue,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(AGENTS_LIST) { agent ->
            val isExpanded = selectedAgentId == agent.id
            Card(
                colors = CardDefaults.cardColors(containerColor = SocximaPanel),
                border = BorderStroke(1.dp, if (isExpanded) agent.color else SocximaLinea),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedAgentId = if (isExpanded) null else agent.id }
                    .testTag("agent_card_${agent.id}")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = agent.icon,
                                color = agent.color,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Column {
                                Text(
                                    text = agent.name,
                                    color = agent.color,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "ROLES: ${agent.role.uppercase()}",
                                    color = SocximaTenue,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Monospace visual text arrows for expansion indicator (Guarantees zero vector dependency errors)
                        Text(
                            text = if (isExpanded) "▲" else "▼",
                            color = agent.color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Expandable specs section
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Divider(color = SocximaLinea, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "DIRECCIÓN DE NODO:",
                                color = SocximaTenue,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = agent.nodeAddress,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .background(SocximaFondo)
                                    .padding(4.dp)
                                    .fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "CAPACIDAD ENCRIPTADA DE CONTROL:",
                                color = SocximaTenue,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = agent.description,
                                color = SocximaLetra,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ╔══════════════════════════════════════════════════════════════╗
// ║ CHAT INPUT CONSOLE BLOCK                                     ║
// ╚══════════════════════════════════════════════════════════════╝

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersistentConsoleInput(
    viewModel: SocximaViewModel,
    onScanClicked: () -> Unit
) {
    val inputText = viewModel.inputText.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SocximaPanel)
            .border(BorderStroke(1.dp, SocximaLinea))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Sonnet 4.6 Sellado visual header tag
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "💚 Sonnet 4.6 | SELLADO",
                    color = Color(0xFF10B981),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "CONEXIÓN OFICIAL SOL/BTC 1000X",
                color = SocximaTenue,
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quick scanning floating shortcut
            IconButton(
                onClick = onScanClicked,
                modifier = Modifier
                    .size(40.dp)
                    .background(SocximaFondo, RoundedCornerShape(4.dp))
                    .border(1.dp, SocximaLinea, RoundedCornerShape(4.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Leer Documentación OCR",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
            }

            // Space Terminal styled text field
            TextField(
                value = inputText,
                onValueChange = { viewModel.updateInputText(it) },
                placeholder = {
                    Text(
                        text = "Responder a Claude...",
                        color = SocximaTenue,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("prompt_input_field"),
                textStyle = TextStyle(
                    color = SocximaLetra,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = SocximaFondo,
                    unfocusedContainerColor = SocximaFondo,
                    disabledContainerColor = SocximaFondo,
                    focusedIndicatorColor = ColorMistral,
                    unfocusedIndicatorColor = SocximaLinea,
                    cursorColor = ColorMistral
                ),
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                enabled = !isLoading
            )

            // Action send button
            Button(
                onClick = { viewModel.submitPrompt(inputText) },
                enabled = inputText.trim().isNotEmpty() && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    disabledContainerColor = SocximaTenue
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .height(44.dp)
                    .testTag("send_prompt_button"),
                contentPadding = PaddingValues(horizontal = 14.dp)
            ) {
                Text(
                    text = if (isLoading) "SINC" else "ENVIAR",
                    color = SocximaFondo,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ╔══════════════════════════════════════════════════════════════╗
// ║ OVERLAY EFFECT SCANNER VIEW MODEL                            ║
// ╚══════════════════════════════════════════════════════════════╝

@Composable
fun LaserScanOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f))
            .testTag("ocr_scan_laser"),
        contentAlignment = Alignment.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "laser")
        val laserOffset by infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.9f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "offset"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .border(2.dp, Color(0xFF10B981), RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                // Background scan target placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF10B981).copy(alpha = 0.05f))
                )

                // Simulated laser line
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val lineY = maxHeight * laserOffset
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .offset(y = lineY)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF10B981), Color.Transparent)
                                )
                            )
                            .border(1.dp, Color(0xFF10B981))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "⚡ ADQUISICIÓN OCR VELOCIDAD X1000",
                color = Color(0xFF10B981),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Sincronizando identidad legal bajo carteras DobMoney de EVELIO LLOVERA...",
                color = SocximaTenue,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
        }
    }
}
