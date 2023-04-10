package com.teachmint.teachmint.ui.classroom.homework

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.media.MediaPlayer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.teachmint.domain.entities.AttachmentDetails
import com.teachmint.domain.entities.homework.*
import com.teachmint.krayon.atom.*
import com.teachmint.krayon.molecule.*
import com.teachmint.krayon.organism.BottomSheet
import com.teachmint.krayon.organism.DatePickerView
import com.teachmint.krayon.organism.TimePickerview
import com.teachmint.ktor.rememberMutableState
import com.teachmint.teachmint.R
import com.teachmint.teachmint.ui.classroom.homework.attempt.QuestionStatus
import com.teachmint.teachmint.ui.classroom.homework.attempt.QuestionsGridView
import com.teachmint.teachmint.util.*
import timber.log.Timber
import java.io.File

/**
 * Created by Tushar Garg on 05/10/22.
 */

enum class VoiceRecordingStatus {
    Recording, Idle, Paused, RequestRecording
}

enum class HomeworkCreationUISheets {
    None, SubmissionType, AttachmentType, AssignBottomSheet, PracticeQuestionType, PermissionsSheet, AddQuestionType, LinkToLesson
}

enum class HomeworkEvaluationUISheets {
    None, Feedback, SwitchQuestion, AttachmentType, PermissionsSheet
}

enum class HomeworkSchedulingTypes {
    AssignNow, ScheduleForLater
}

enum class FileSelectionCategory {
    None, Question, Solution
}

enum class SuccessBottomSheetUI {
    None, SuccessWithAttachment, UploadingStage, SuccessWithoutAttachment, SubmitConfirmation
}

@Composable
fun HomeworkCreationAppBar(type: HomeworkType) {
    var icon = Icons.Filled.Subject
    var headerText = stringResource(id = R.string.text_learning)
    var subHeaderText = stringResource(id = R.string.get_student_to_read_text)
    var bgColor = KrayonColors.tmMorningGlory10
    when (type) {
        HomeworkType.LEARNING -> {
            //use defaults
        }
        HomeworkType.PRACTICE -> {
            icon = Icons.Filled.Edit
            headerText = stringResource(id = R.string.practice)
            bgColor = KrayonColors.tmBiloba10
            subHeaderText = stringResource(id = R.string.get_student_to_practice_text)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
    ) {
        Spacer(modifier = Modifier.height(LocalConfiguration.current.screenHeightDp.dp * .02f))

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (header, subHeader, cross) = createRefs()


            Row(
                modifier = Modifier.constrainAs(header) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, "")
                Text(
                    modifier = Modifier.padding(start = Dimens._8),
                    text = headerText,
                    style = KrayonTheme.typography.h4_18_semi_bold,
                )
            }


            Text(text = subHeaderText,
                style = KrayonTheme.typography.body_12_medium,
                textAlign = TextAlign.Center,
                color = KrayonColors.tmGrey70,
                modifier = Modifier.constrainAs(subHeader) {
                    top.linkTo(header.bottom, margin = 16.dp)
                    start.linkTo(parent.start, margin = 40.dp)
                    end.linkTo(parent.end, margin = 40.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                })

            IconButton(
                onClick = onBackPressed,
                modifier = Modifier.constrainAs(cross) {
                    end.linkTo(parent.end, margin = 16.dp)
                }
            ) {

                Icon(Icons.Filled.Close, "")
            }


        }
    }
}

@Composable
fun MarksErrorCard(){
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(color = KrayonColors.tmCalico10), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Outlined.ErrorOutline, "", tint = KrayonColors.tmSemanticWarning100, modifier = Modifier.padding(end = Dimens._8, top = Dimens._8, bottom  = Dimens._8))
        Text(stringResource(id = R.string.update_marks_error), style = KrayonTheme.typography.body_12_medium, color = KrayonColors.tmSemanticWarning100, modifier = Modifier.padding(vertical = Dimens._8))
    }
}

@Composable
fun AddMarksLayout(
    homework: Homework,
    eventListener: (HomeworkCreationUIEvents) -> Unit,
    updateToggleStatus: (Boolean) -> Unit = {}
) {
    val addMarksToggle = rememberMutableState(value = homework.totalMarks != 0.0)
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.padding(Dimens.marginScreen),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KrayonToggle(
                onToggled = {
                    addMarksToggle.value = it
                    updateToggleStatus(it)
                },
                initialToggleState = addMarksToggle.value
            )
            Text(
                modifier = Modifier.padding(start = Dimens._8),
                text = stringResource(id = R.string.add_marks),
                style = KrayonTheme.typography.body_14_semibold,
            )
        }


        AnimatedVisibility(visible = (addMarksToggle.value && homework.getHwType() == HomeworkType.LEARNING)) {
            EditMarks(marks = homework.totalMarks?.toInt().toString()) {
                eventListener(HomeworkCreationUIEvents.UpdateMarks(it))
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditMarks(modifier: Modifier = Modifier, marks: String, onMarksUpdated: (Double) -> Unit) {
    val marksEdit = rememberMutableState(value = marks)
    val maxChar = 5
    val keyboardController = LocalSoftwareKeyboardController.current
    Card(
        modifier = modifier.padding(end = Dimens._20),
        backgroundColor = KrayonColors.tmGrey20,
        elevation = Dimens._0
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier
                    .widthIn(max = Dimens._64)
                    .heightIn(max = Dimens._44)
                    .padding(vertical = Dimens._12),
                value = marksEdit.value,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.NumberPassword
                ),
                keyboardActions = KeyboardActions(
                    onDone = {keyboardController?.hide()}),
                maxLines = 1,
                onValueChange = {
                    if (it.length <= maxChar){
                        marksEdit.value = it
                        val marksInt = try {
                            it.toDouble()
                        } catch (e: Exception) {
                            0.0
                        }
                        onMarksUpdated(marksInt)}
                },
                textStyle = KrayonTheme.typography.body_14_regular.copy(textAlign = TextAlign.Center),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        if (marksEdit.value.isEmpty()) {
                            Text(
                                text = "Eg. 100",
                                style = KrayonTheme.typography.body_14_medium,
                                color = KrayonColors.tmGrey40
                            )
                        }
                        innerTextField()
                    }
                }
            )

            Text(
                modifier = Modifier.padding(horizontal = Dimens._8),
                text = stringResource(id = R.string.marks),
                style = KrayonTheme.typography.body_14_regular,
            )

            Spacer(Modifier.width(Dimens._8))

        }

    }
}


fun String.isDocument(): Boolean {
    val docTypes: List<String> = listOf("PDF")
    return docTypes.contains(this)
}

fun String.isImage(): Boolean {
    val imageTypes: List<String> = listOf("JPG", "JPEG", "PNG")
    return imageTypes.contains(this)
}

fun String.isAudio(): Boolean {
    val audioTypes: List<String> = listOf("MP3")
    return audioTypes.contains(this)
}

fun getIconForSubmissionType(type: QuestionType): ImageVector {
    return when (type) {
        QuestionType.MARK_READ -> Icons.Filled.CheckCircle
        QuestionType.OFFLINE -> Icons.Filled.Grading
        QuestionType.TYPE_ANSWER -> Icons.Filled.TextFields
        QuestionType.AUDIO -> Icons.Filled.Mic
        else -> Icons.Filled.TextFields
    }
}

fun getTitleForSubmissionType(type: QuestionType): String {
    return when (type) {
        QuestionType.MARK_READ -> "Mark as completed"
        QuestionType.OFFLINE -> "Offline Submission"
        QuestionType.TYPE_ANSWER -> "Type answer or attach file"
        QuestionType.AUDIO -> "Audio"
        else -> "Type answer or attach file"
    }
}

fun getAttemptCardHeader(type: QuestionType): String {
    return when (type) {
        QuestionType.MARK_READ -> "Mark as completed"
        QuestionType.OFFLINE -> "Offline Submission"
        QuestionType.TYPE_ANSWER -> "SUBJECTIVE"
        QuestionType.AUDIO -> "AUDIO"
        QuestionType.MCQ -> "MCQ"
        QuestionType.SUBJECTIVE -> "SUBJECTIVE"
        QuestionType.DICTATION -> "DICTATION"
        QuestionType.PRONUNCIATION -> "PRONUNCIATION"
    }
}

fun getKeyForSubmissionType(type: QuestionType): String {
    return when (type) {
        QuestionType.MARK_READ -> "MARK_READ"
        QuestionType.OFFLINE -> "OFFLINE"
        QuestionType.TYPE_ANSWER -> "TYPE_ANSWER"
        QuestionType.AUDIO -> "AUDIO"
        else -> "TYPE_ANSWER"
    }
}

@Composable
fun OpenPreviewDialog(path: String, onClose: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x96212427))
            .clickable { onClose() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(Dimens._20), horizontalArrangement = Arrangement.End
        ) {
            Icon(Icons.Filled.Close, tint = KrayonColors.tmGrey10, contentDescription = "")
        }
        AppImage(
            path,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(Dimens._8))
        )

    }

}

@Composable
fun DateTimeSelectionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.DateRange,
    header: String = stringResource(id = R.string.set_deadline),
    defaultText: String,
    isDate: Boolean = true,
    onChanged: (String) -> Unit
) {
    val selectedTime = rememberMutableState(value = defaultText)

    Column(
        modifier
            .padding(Dimens._4)
            .clickable {
                if (isDate) DatePickerView(header) {
                    selectedTime.value = it ?: defaultText
                    onChanged(it ?: selectedTime.value)
                } else TimePickerview(defaultText) {
                    selectedTime.value = it ?: defaultText
                    onChanged(it ?: selectedTime.value)
                }
            }) {
        Text(
            modifier = Modifier.padding(bottom = Dimens._8),
            text = header,
            style = KrayonTheme.typography.body_12_medium,
            color = KrayonColors.tmGrey70
        )
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            backgroundColor = KrayonColors.tmGrey20,
            elevation = Dimens._0
        ) {
            val displayText = selectedTime.value
            Row(
                modifier = Modifier
                    .padding(horizontal = Dimens._16, vertical = Dimens._12)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, "", Modifier.size(Dimens._16))
                Spacer(Modifier.width(Dimens._4))
                Text(
                    modifier = Modifier.padding(start = Dimens._8),
                    text = displayText,
                    style = KrayonTheme.typography.body_14_regular,
                )
            }
        }
    }
}


@Composable
fun HomeworkOverview(homework: Homework) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens._16)
    ) {
        HomeworkOverviewCard(
            modifier = Modifier
                .weight(1f)
                .padding(end = Dimens._6), homework, HomeworkOverviewCardTypes.Deadline
        )

        HomeworkOverviewCard(
            modifier = Modifier
                .weight(1f)
                .padding(start = Dimens._6), homework, HomeworkOverviewCardTypes.Marks
        )

    }
}

enum class HomeworkOverviewCardTypes {
    Deadline, Marks
}

@Composable
fun HomeworkOverviewCard(modifier: Modifier, homework: Homework, type: HomeworkOverviewCardTypes) {
    var imageModel = R.drawable.ic_hw_deadline
    var bgColor = KrayonColors.tmGrey10
    var mainText = "Aug 11, 1:30 AM "
    var desc = stringResource(id = R.string.deadline_text)
    var marks = if (homework.totalMarks == null || !homework.gradedAssessment)  stringResource(id = R.string.no_marks_text)
    else homework.totalMarks.toString()
    when (type) {
        HomeworkOverviewCardTypes.Deadline -> {
            imageModel = R.drawable.ic_hw_deadline
            mainText = convertTimestamptoMMMddhmma(homework.endTime.toLong())
            desc = stringResource(id = R.string.deadline_text)
        }
        HomeworkOverviewCardTypes.Marks -> {
            if(homework.isEvaluated() && homework.gradedAssessment){
                marks = "${homework.submission?.marks.toString()}/$marks"
                desc = stringResource(id = R.string.marks)
            }else {
                desc = stringResource(id = R.string.total_marks)
            }
            imageModel = R.drawable.ic_hw_marks
            mainText = marks

            if(homework.isEvaluated()) bgColor = KrayonColors.tmFeioja10
        }
    }

    Card(
        modifier = modifier,
        backgroundColor = bgColor,
        shape = RoundedCornerShape(Dimens._8)
    ) {
        Column(Modifier.padding(horizontal = Dimens._16, vertical = Dimens._12)) {
            AppImage(model = painterResource(id = imageModel))
            Text(
                mainText,
                modifier = Modifier.padding(top = Dimens._8),
                style = KrayonTheme.typography.body_14_semibold,
                color = KrayonColors.tmGrey100
            )
            Text(
                desc,
                modifier = Modifier.padding(top = Dimens._4),
                style = KrayonTheme.typography.body_12_medium,
                color = KrayonColors.tmGrey70
            )
        }
    }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OpenQuestionSelectionSheet(onDismiss: () -> Unit, attemptStudent:  StudentAttempt, currentQuestionIndex: MutableState<Int>){
    BottomSheet(
        title = stringResource(id = R.string.switch_questions),
        onDismiss = { onDismiss() }
    ) {
        val map = mutableMapOf<Int, QuestionStatus>()
        for(index in attemptStudent.attempt.indices){
            if(attemptStudent.attempt[index].isEvaluated()){
                map[index+1] = QuestionStatus.Evaluated
            } else {
                map[index+1] = QuestionStatus.NotEvaluated
            }
        }
        QuestionsGridView(onDismiss, map ,currentQuestionIndex)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun OpenQuestionAttemptSheet(onDismiss: () -> Unit, currentQuestionIndex: MutableState<Int>, answerStatusMap : MutableMap<Int, QuestionStatus>){
    BottomSheet(
        title = stringResource(id = R.string.switch_questions),
        onDismiss = { onDismiss() }
    ) {
        QuestionsGridView(onDismiss, answerStatusMap ,currentQuestionIndex)
    }
}

fun urlAndUriPlayer(audioPlayer: MediaPlayer, context: Context, uri: MutableState<String>, url: MutableState<String>)
{
    if(uri.value != "" || url.value != ""){
        try {
            if (audioPlayer.isPlaying) {
                //    audioPlayer.pause()
                audioPlayer.reset()
            }
            if (uri.value != "") audioPlayer.setDataSource(context, Uri.parse(uri.value))
            else if (url.value != "") audioPlayer.setDataSource(url.value)
            audioPlayer.prepare()
            audioPlayer.start()
            audioPlayer.setOnCompletionListener {
                uri.value = ""
                url.value = ""
            }
        } catch (e: Exception) {

        }
    } else {
        audioPlayer.reset()
    }
}
fun uriPlayer(audioPlayer: MediaPlayer, context: Context, uri: MutableState<String>){
    if(uri.value != ""){
        try {
            if (audioPlayer.isPlaying) {
                audioPlayer.reset()
            }
            audioPlayer.setDataSource(context, Uri.parse(uri.value))
            audioPlayer.prepare()
            audioPlayer.start()
            audioPlayer.setOnCompletionListener {
                uri.value = ""
            }
        } catch (e: Exception) {

        }
    } else {
        audioPlayer.reset()
    }
}

fun urlPlayer(audioPlayer: MediaPlayer, url: MutableState<String>){
    if(url.value != ""){
        try {
            if(audioPlayer.isPlaying) {
                audioPlayer.reset()
            }
            audioPlayer.setDataSource(url.value)
            audioPlayer.prepare()
            audioPlayer.start()
            audioPlayer.setOnCompletionListener {
                url.value = ""
                audioPlayer.reset()
            }
        } catch (e: Exception) {

        }

    } else {
        audioPlayer.reset()
    }
}

fun getAttachmentDetails(list: List<TmText>?): List<AttachmentDetails> {
    if (list.isNullOrEmpty()) return listOf()
    val finalList = mutableListOf<AttachmentDetails>()

    list.filter { it.isAttachment() }.forEach {
        finalList.add(
            AttachmentDetails(
                uri = it.value.getFileUri(),
                extension = it.value.type!!,
                attachmentId = it.value.attachmentId
            )
        )
    }

    return finalList

}

fun getNumberOfPages(uri: Uri) : Int{
    val file = File(uri.path!!)
    var numPages = 0
    val parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    var pdfRenderer: PdfRenderer? = null
    try {
        pdfRenderer = PdfRenderer(parcelFileDescriptor)
        numPages = pdfRenderer.pageCount
        pdfRenderer.close()
        parcelFileDescriptor.close()

    } catch(e:Exception){
        Timber.d("PDF error exception: HomeworkCommonUI")
    }
    return numPages
}


@Composable
fun ShowYoutubeCard(openYoutubeVideo: () -> Unit){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Dimens._12, bottom = Dimens._6)
            .clickable { openYoutubeVideo() },
        shape = RoundedCornerShape(Dimens._12),
        backgroundColor = KrayonColors.tmBaseDarkBlue200,
        elevation = Dimens._0
    ) {
        Row( Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.padding(Dimens._12)) {
                Icon(
                    imageVector = Icons.Filled.Stars,
                    contentDescription = "",
                    tint = KrayonColors.tmSemanticWarning100
                )
                Text(
                    "Learn what’s new",
                    style = KrayonTheme.typography.body_14_semibold,
                    color = KrayonColors.tmGrey10,
                    modifier = Modifier.padding(start = Dimens._12)
                )
            }
            Card(
                modifier = Modifier.padding(Dimens._6),
                shape = RoundedCornerShape(Dimens._12),
                backgroundColor = KrayonColors.tmBaseDarkBlue200,
                elevation = Dimens._0,
                border = BorderStroke(Dimens._1, KrayonColors.tmGrey10)
            ){
                Row(Modifier.padding(Dimens._6), verticalAlignment = Alignment.CenterVertically){
                    Icon(
                        imageVector = Icons.Outlined.PlayCircle,
                        contentDescription = "",
                        tint = KrayonColors.tmGrey10
                    )
                    Text(
                        "Watch",
                        style = KrayonTheme.typography.body_14_semibold,
                        color = KrayonColors.tmGrey10,
                        modifier = Modifier.padding(start = Dimens._8, end = Dimens._2)
                    )
                }
            }
        }
    }
}
