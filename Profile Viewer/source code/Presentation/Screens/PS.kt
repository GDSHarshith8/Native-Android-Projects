package com.profileviewer.Presentation.Screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.profileviewer.Presentation.ViewModel.ProfileViewModel
import com.profileviewer.Presentation.ViewModel.UiState
import com.profileviewer.network.User

fun openLink(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialSegmentedButtons(user: User) {
    val context = LocalContext.current

    SingleChoiceSegmentedButtonRow {

        SegmentedButton(
            selected = false,
            onClick = {
                openLink(context, user.social.website)
            },
            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
        ) {
            Icon(Icons.Default.Language, contentDescription = "Website")
        }

        SegmentedButton(
            selected = false,
            onClick = {
                user.social.profiles
                    .firstOrNull { it.platform == "Instagram" }
                    ?.url
                    ?.let { openLink(context, it) }
            },
            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
        ) {
            Icon(Icons.Outlined.CameraAlt, contentDescription = "Instagram")
        }

        SegmentedButton(
            selected = false,
            onClick = {
                user.social.profiles
                    .firstOrNull { it.platform == "Facebook" }
                    ?.url
                    ?.let { openLink(context, it) }
            },
            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
        ) {
            Icon(Icons.Default.Facebook, contentDescription = "Facebook")
        }
    }
}

@Composable
fun FollowersFollowingRow(
    followers: Int,
    following: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Followers
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = followers.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Followers",
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 1.sp
            )
        }

        VerticalDivider(
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp)
        )

        // Following
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = following.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Following",
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 1.sp
            )
        }
    }
}


@Composable
fun MediaRow(
    selectedMedia: String,
    onMediaSelected: (String) -> Unit
) {
    val mediaTypes = listOf("Stories", "Posts")

    Row(
        modifier = Modifier
            .wrapContentWidth()
            .padding(10.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(34.dp)
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        mediaTypes.forEach { media ->
            val isSelected = media == selectedMedia

            FilterChip(
                selected = isSelected,
                onClick = { onMediaSelected(media) },
                label = {
                    Text(text = media)
                },
                elevation = FilterChipDefaults.filterChipElevation(
                    elevation = if (isSelected) 2.dp else 0.dp
                ),
                border = if (isSelected)
                    BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                else null,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer
                    else Color.Transparent,
                    labelColor = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .height(44.dp)
                    .padding(4.dp),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun GridItem(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Makes the item square
            .background(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.small)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun MediaGrid(
    selectedMedia: String
) {
    val sampleItems =
        (1..20).map {
            if (selectedMedia == "Stories") "Story $it" else "Post $it"
        }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(sampleItems) { item ->
            GridItem(text = item)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(viewModel: ProfileViewModel) {
    val state by viewModel.uiState

    var selectedMedia by rememberSaveable { mutableStateOf("Stories") }

    when (state) {
        is UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is UiState.Success -> {
            val user = (state as UiState.Success).user

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            Glide.with(this)
                                .load(user.avatar)
                                .circleCrop()
                                .into(this)
                        }
                    },
                    modifier = Modifier.size(120.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(user.name, style = MaterialTheme.typography.titleLarge)
                Text("${user.location.city}, ${user.location.country}", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(20.dp))

                SocialSegmentedButtons(user)

                FollowersFollowingRow(
                    followers = user.statistics.followers,
                    following = user.statistics.following
                )

                MediaRow(
                    selectedMedia = selectedMedia,
                    onMediaSelected = { selectedMedia = it }
                )

                MediaGrid(selectedMedia = selectedMedia)
            }
        }

        else -> Unit
    }
}