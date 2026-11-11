package com.example.tvtime.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tvtime.notifications.NotificationHelper
import com.example.tvtime.notifications.NotificationScheduler
import com.example.tvtime.theme.TvTimeBackground
import com.example.tvtime.theme.TvTimeBorder
import com.example.tvtime.theme.TvTimeOnYellow
import com.example.tvtime.theme.TvTimeSurface
import com.example.tvtime.theme.TvTimeSurfaceVariant
import com.example.tvtime.theme.TvTimeTextMuted
import com.example.tvtime.theme.TvTimeTextPrimary
import com.example.tvtime.theme.TvTimeTextSecondary
import com.example.tvtime.theme.TvTimeYellow

@Composable
fun SettingsDialog(
    currentApiKey: String,
    onSaveApiKey: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var morningEnabled by remember {
        mutableStateOf(NotificationScheduler.isMorningAlertsEnabled(context))
    }
    var seasonEnabled by remember {
        mutableStateOf(NotificationScheduler.isSeasonAlertsEnabled(context))
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = TvTimeSurface,
        title = {
            Text(
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TvTimeTextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TMDB API Key Section
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            tint = TvTimeYellow
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TMDB API v3 Key",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TvTimeTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Leave empty to use curated offline sample data or provide your free TMDB API key to search live TV shows.",
                        fontSize = 12.sp,
                        color = TvTimeTextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. 1a2b3c4d5e...", color = TvTimeTextMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TvTimeYellow,
                            unfocusedBorderColor = TvTimeBorder,
                            focusedTextColor = TvTimeTextPrimary,
                            unfocusedTextColor = TvTimeTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { onSaveApiKey(apiKeyInput.trim()) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TvTimeYellow,
                                contentColor = TvTimeOnYellow
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(text = "Save Key", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                HorizontalDivider(color = TvTimeBorder)

                // Notifications Section
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = TvTimeYellow
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notifications",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TvTimeTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Morning alert toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Morning Episode Alert",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TvTimeTextPrimary
                            )
                            Text(
                                text = "Daily at 8:00 AM when a tracked show airs today",
                                fontSize = 12.sp,
                                color = TvTimeTextMuted
                            )
                        }
                        Switch(
                            checked = morningEnabled,
                            onCheckedChange = {
                                morningEnabled = it
                                NotificationScheduler.setMorningAlertsEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TvTimeOnYellow,
                                checkedTrackColor = TvTimeYellow
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 1-Week season premiere toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Season Premiere Alert",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = TvTimeTextPrimary
                            )
                            Text(
                                text = "Notifies 1 week before a new season starts",
                                fontSize = 12.sp,
                                color = TvTimeTextMuted
                            )
                        }
                        Switch(
                            checked = seasonEnabled,
                            onCheckedChange = {
                                seasonEnabled = it
                                NotificationScheduler.setSeasonAlertsEnabled(context, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = TvTimeOnYellow,
                                checkedTrackColor = TvTimeYellow
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Test Notifications:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TvTimeTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                NotificationHelper.sendMorningEpisodeAlert(
                                    context,
                                    "Severance",
                                    "S02E01",
                                    "Hello Kier",
                                    101
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TvTimeBorder)
                        ) {
                            Text(text = "Test Airing", fontSize = 12.sp, color = TvTimeYellow)
                        }

                        OutlinedButton(
                            onClick = {
                                NotificationHelper.sendSeasonPremiereAlert(
                                    context,
                                    "Fallout",
                                    2,
                                    7,
                                    102
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TvTimeBorder)
                        ) {
                            Text(text = "Test Premiere", fontSize = 12.sp, color = TvTimeYellow)
                        }
                    }
                }

                HorizontalDivider(color = TvTimeBorder)

                // Version info
                Text(
                    text = "TV Time Clone v1.0.0 (Material 3)",
                    fontSize = 11.sp,
                    color = TvTimeTextMuted
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Close", color = TvTimeYellow, fontWeight = FontWeight.Bold)
            }
        }
    )
}
