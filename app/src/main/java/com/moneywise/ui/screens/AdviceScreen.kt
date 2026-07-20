package com.moneywise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

enum class ProfileType(val label: String, val description: String) {
    STUDENT("Student", "Ik studeer nog"),
    ADULT("Volwassene", "Ik werk fulltime")
}

enum class Level(val label: String, val description: String) {
    BEGINNER("Beginner", "Nog niet eerder gespaard of belegd"),
    INTERMEDIATE("Gemiddeld", "Spaar al, maar beleg nog niet"),
    ADVANCED("Gevorderd", "Beleg al actief")
}

data class AdviceItem(
    val title: String,
    val content: String
)

fun getAdvice(profile: ProfileType, level: Level): List<AdviceItem> {
    return when (profile) {
        ProfileType.STUDENT -> when (level) {
            Level.BEGINNER -> listOf(
                AdviceItem(
                    "Begin met een noodfonds",
                    "Zet elke maand een klein bedrag apart (ook €25 telt) op een spaarrekening. Doel: 1-2 maanden kosten als buffer."
                ),
                AdviceItem(
                    "Houd je uitgaven bij",
                    "Gebruik een app of spreadsheet om te zien waar je geld naartoe gaat. Je zult verrassende patronen ontdekken."
                ),
                AdviceItem(
                    "Gebruik studentenvoordelen",
                    "Veel banken en verzekeraars bieden gratis studentenpakketten. Profiteer hiervan!"
                ),
                AdviceItem(
                    "Leer de basis van geld",
                    "Lees een beginnersboek over geldzaken of volg een gratis online cursus. Kennis is je beste investering."
                )
            )
            Level.INTERMEDIATE -> listOf(
                AdviceItem(
                    "Start met indexfondsen",
                    "Zet een vast bedrag per maand in een wereldwijd indexfondsen (bijv. VWRL of S&P500). Laag risico, gemiddeld rendement."
                ),
                AdviceItem(
                    "Gebruik je studiefinanciering slim",
                    "Als je lening krijgt, overweeg een deel te beleggen in plaats van alles direct uit te geven. De rente is laag."
                ),
                AdviceItem(
                    "Denk aan je pensioen",
                    "Begin nu met een klein bedrag beleggen. Door rente-op-rente heb je over 40 jaar een groot verschil."
                ),
                AdviceItem(
                    "Zoek naar beurzen",
                    "Veel organisaties bieden beurzen aan voor studenten. Gratis geld is het beste geld!"
                )
            )
            Level.ADVANCED -> listOf(
                AdviceItem(
                    "Optimaliseer je portefeuille",
                    "Spreid over verschillende sectoren en regio's. Overweeg een mix van aandelen, obligaties en alternative investments."
                ),
                AdviceItem(
                    "Leer over belastingvoordelen",
                    "Gebruik je jaarruimte en reserveringsruimte voor pensioenbeleggen. Dit is fiscaal voordelig."
                ),
                AdviceItem(
                    "Bouw een passief inkomen op",
                    "Onderzoek dividend aandelen of ETF's die regelmatig uitkeren. Dit kan je studiekosten helpen betalen."
                ),
                AdviceItem(
                    "Risicobeheer",
                    "Zorg dat je nooit meer belegt dan je kunt missen. Houd altijd een noodfonds apart."
                )
            )
        }
        ProfileType.ADULT -> when (level) {
            Level.BEGINNER -> listOf(
                AdviceItem(
                    "Bouw eerst een noodfonds",
                    "Sparen voor 3-6 maanden vaste lasten op een spaarrekening. Dit is je financiële veiligheidsnet."
                ),
                AdviceItem(
                    "Betaal eerst dure schulden af",
                    "Creditcards, persoonlijke leningen met hoge rente. Elke euro schuld met 10% rente kost je geld."
                ),
                AdviceItem(
                    "Automatiseer je spaargedrag",
                    "Maak een automatische overschrijving naar je spaarrekening op betaaldag. Wat je niet ziet, geef je niet uit."
                ),
                AdviceItem(
                    "Begin klein met beleggen",
                    "Zodra je buffer staat, start met een klein bedrag per maand in een indexfonds. €50/maand is al genoeg."
                )
            )
            Level.INTERMEDIATE -> listOf(
                AdviceItem(
                    "Bouw je pensioen op",
                    "Gebruik je jaarruimte voor pensioenbeleggen. Dit levert gemiddeld 5-7% rendement op en is fiscaal voordelig."
                ),
                AdviceItem(
                    "Overweeg een mix van sparen en beleggen",
                    "Houd 30-50% op een spaarrekening (veilig), beleg de rest voor groei op langere termijn."
                ),
                AdviceItem(
                    "Kijk naar je hypotheek",
                    "Extra aflossen kan lonend zijn, maar beleggen rendeert vaak beter. Bereken wat voordeliger is voor jou."
                ),
                AdviceItem(
                    "Verhoog je inleg als je salaris stijgt",
                    "Elke salarisverhoging: 50% ervan naar beleggen. Zo bouw je vermogen op zonder in te leveren."
                )
            )
            Level.ADVANCED -> listOf(
                AdviceItem(
                    "Optimaliseer je belastingpositie",
                    "Gebruik alle fiscale mogelijkheden: jaarruimte, reserveringsruimte, groen sparen. Laat geen geld liggen."
                ),
                AdviceItem(
                    "Spreid je investeringen",
                    "Mix van aandelen, obligaties, onroerend goed (REITs), en alternative investments. Niet alles in één mandje."
                ),
                AdviceItem(
                    "Bouw een financiële planning op",
                    "Bepaal je financiële doelen op korte (1-3 jaar), middellange (3-10 jaar) en lange termijn (10+ jaar)."
                ),
                AdviceItem(
                    "Leer van je fouten",
                    "Houd een beleggingsdagboek bij. Wat werkt, wat niet? Pas je strategie aan op basis van ervaring."
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdviceScreen(
    onBack: () -> Unit
) {
    var selectedProfile by remember { mutableStateOf<ProfileType?>(null) }
    var selectedLevel by remember { mutableStateOf<Level?>(null) }

    val advice = remember(selectedProfile, selectedLevel) {
        if (selectedProfile != null && selectedLevel != null) {
            getAdvice(selectedProfile!!, selectedLevel!!)
        } else emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Advies") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Terug")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Sparen & Beleggen Advies",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Kies je profiel en niveau voor gepersonaliseerd advies.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            HorizontalDivider()

            Text(
                text = "Wat is je situatie?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileType.entries.forEach { type ->
                    FilterChip(
                        selected = selectedProfile == type,
                        onClick = { selectedProfile = type },
                        label = {
                            Column {
                                Text(type.label, fontWeight = FontWeight.Medium)
                                Text(type.description, style = MaterialTheme.typography.bodySmall)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (selectedProfile != null) {
                HorizontalDivider()

                Text(
                    text = "Wat is je ervaring?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Level.entries.forEach { level ->
                        FilterChip(
                            selected = selectedLevel == level,
                            onClick = { selectedLevel = level },
                            label = {
                                Column {
                                    Text(level.label, fontWeight = FontWeight.Medium)
                                    Text(level.description, style = MaterialTheme.typography.bodySmall)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (advice.isNotEmpty()) {
                HorizontalDivider()

                Text(
                    text = "Jouw advies",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                advice.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            if (selectedProfile != null && selectedLevel != null) {
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Text(
                        text = "Disclaimer: Dit advies is algemeen en geen financieel advies. Raadpleeg een financieel adviseur voor persoonlijk advies.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}
