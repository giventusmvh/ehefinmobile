package com.example.ehefin_mobile.feature.loan.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.example.ehefin_mobile.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ehefin_mobile.core.designsystem.theme.StatusApproved
import com.example.ehefin_mobile.core.designsystem.theme.StatusPending
import com.example.ehefin_mobile.core.designsystem.theme.StatusRejected
import com.example.ehefin_mobile.core.designsystem.theme.StatusSubmitted
import com.example.ehefin_mobile.feature.loan.domain.model.LoanStatus

@Composable
fun LoanStatusBadge(
    status: LoanStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor, displayText) = when (status) {
        LoanStatus.SUBMITTED -> Triple(
            StatusSubmitted.copy(alpha = 0.15f),
            StatusSubmitted,
            stringResource(R.string.status_badge_submitted)
        )
        LoanStatus.MARKETING_APPROVED -> Triple(
            StatusPending.copy(alpha = 0.15f),
            StatusPending,
            stringResource(R.string.status_badge_marketing_review)
        )
        LoanStatus.MARKETING_REJECTED -> Triple(
            StatusRejected.copy(alpha = 0.15f),
            StatusRejected,
            stringResource(R.string.status_badge_marketing_rejected)
        )
        LoanStatus.BRANCH_MANAGER_APPROVED -> Triple(
            StatusPending.copy(alpha = 0.15f),
            StatusPending,
            stringResource(R.string.status_badge_bm_review)
        )
        LoanStatus.BRANCH_MANAGER_REJECTED -> Triple(
            StatusRejected.copy(alpha = 0.15f),
            StatusRejected,
            stringResource(R.string.status_badge_bm_rejected)
        )
        LoanStatus.DISBURSED -> Triple(
            StatusApproved.copy(alpha = 0.15f),
            StatusApproved,
            stringResource(R.string.status_badge_approved)
        )
        LoanStatus.REJECTED -> Triple(
            StatusRejected.copy(alpha = 0.15f),
            StatusRejected,
            stringResource(R.string.status_badge_rejected)
        )
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}