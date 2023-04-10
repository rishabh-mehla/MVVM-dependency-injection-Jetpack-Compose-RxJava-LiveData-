package com.teachmint.teachmint.ui.classroom.homework

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.findNavController
import com.teachmint.domain.entities.homework.Homework
import com.teachmint.domain.entities.homework.HomeworkDashboardUIEvents
import com.teachmint.domain.entities.homework.HomeworkType
import com.teachmint.domain.entities.homework.StatusHw
import com.teachmint.krayon.atom.KrayonTheme
import com.teachmint.teachmint.LessonSharedViewModel
import com.teachmint.teachmint.MainActivity
import com.teachmint.teachmint.R
import com.teachmint.teachmint.common.TUtils
import com.teachmint.teachmint.databinding.FragmentDiscoverNewBinding
import com.teachmint.teachmint.ui.classroom.homework.dashboard.HomeworkDashboardUI
import com.teachmint.teachmint.ui.classroom.homework.dashboard.HomeworkViewModel
import com.teachmint.teachmint.ui.lessonPlan.lessonContent.TopicFragmentDirections
import com.teachmint.teachmint.util.safeNavigate
import com.teachmint.uploader.data.UploadInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow

/**
 * Created by Tushar Garg on 27/09/22.
 */
@AndroidEntryPoint
class HomeworkFragment : Fragment() {
    private lateinit var binding: FragmentDiscoverNewBinding
    private val lessonSharedViewModel: LessonSharedViewModel by activityViewModels()

    companion object{

        @JvmStatic
        fun newInstance(classId: String, lessonId: String) =
            HomeworkFragment().apply {
                arguments = Bundle().apply {
                    putString(HomeworkNavigationParams.LESSON_ID_PARAM, lessonId)
                    putString(HomeworkNavigationParams.CLASS_ID_PARAM, classId)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDiscoverNewBinding.inflate(layoutInflater)

        setUpComposeViews()
        return binding.root
    }

    override fun onResume() {
        //new creation DL
        super.onResume()
    }

    private fun setUpComposeViews() {
        binding.rootCompose.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val viewModel = hiltViewModel<HomeworkViewModel>()
                val homeworkListState = viewModel.homeworkListFlow.collectAsState()
                val classroomListState = viewModel.classRoomCopyList.collectAsState()
                KrayonTheme {
                    HomeworkDashboardUI(
                        homeworkListState = homeworkListState,
                        classListState = classroomListState,
                        isStudent = TUtils.getUser()?.isStudentOrParent() == true,
                        shareAccess =  HomeworkFragmentArgs.fromBundle(requireArguments()).sharePermission,
                        lessonSharedViewModel.getLessonSharedData(),
                        lessonId = HomeworkFragmentArgs.fromBundle(requireArguments()).lessonId,
                        getUploadInfo = {viewModel.getUploadInfoFlow(context,it)},
                        linkToLessonClicked = {navigateToLinkToLesson(it)},
                    ){event ->
                        when(event) {
                            is HomeworkDashboardUIEvents.NavigateToDraftDashboard -> {
                                navigateToDraftDashboard()
                            }
                            is HomeworkDashboardUIEvents.OnHomeworkClicked -> {
                                onHomeworkClicked(event.homework)
                            }
                            is HomeworkDashboardUIEvents.OnBackPress -> {
                                findNavController().popBackStack()
                            }
                            is HomeworkDashboardUIEvents.OnHomeworkStatusChanged -> {
                                viewModel.updateHomeworkStatus(event.homeworkId,event.status)
                            }
                            is HomeworkDashboardUIEvents.OnHomeworkDeleted -> {
                                viewModel.deleteHomework(event.classId,event.homeworkId)
                            }
                            is HomeworkDashboardUIEvents.OnHomeworkCopied -> {
                                viewModel.copyHomework(event.classIds,event.homeworkId)
                            }
                            is HomeworkDashboardUIEvents.CreateHomeworkClicked -> {
                                navigateToCreation(type = event.type)
                            }
                            is HomeworkDashboardUIEvents.OpenYoutubeVideo -> {
                                viewModel.openYoutubeVideo()
                            }
                            is HomeworkDashboardUIEvents.OnShareClicked -> {
                                viewModel.shareClassroomClicked(event.value,event.homeworkId)
                            }
                            is HomeworkDashboardUIEvents.BackendEventClicked -> {
                                viewModel.sendBackendEvents(event.eventId, event.paramMap)
                            }
                            is HomeworkDashboardUIEvents.CheckDeepLink -> {
                                if(MainActivity.activity != null){
                                    if (MainActivity.activity?.action == "create" && TUtils.getUser()?.isStudentOrParent() == false) {
                                        val isPractice =
                                            MainActivity.activity?.actionType.toString() == "PRACTICE"
                                        navigateToCreation(type = if (isPractice) HomeworkType.PRACTICE else HomeworkType.LEARNING)
                                        MainActivity.activity?.action = null
                                        MainActivity.activity?.actionType = null
                                    }

                                    //attempt and evaluate DL
                                    if ((MainActivity.activity?.action == "open" &&  MainActivity.activity?.tfileId != null) || (MainActivity.activtyParams.tfile!= null)) {
                                        val id = if(MainActivity.activtyParams.tfile?._id !=  null) MainActivity.activtyParams.tfile?._id else MainActivity.activity?.tfileId
                                        MainActivity.activtyParams.tfile = null
                                        MainActivity.activity?.tfileId = null
                                        MainActivity.activity?.action = null
                                        navigateToSubmission(id ?: "")
                                    }}
                            }
                            else -> {}
                        }

                    }
                }

            }
        }
    }

    private fun onHomeworkClicked(homework: Homework) {
        when(homework.status()){
            StatusHw.Draft -> navigateToCreation(homework.id, homework.getHwType())
            StatusHw.Completed -> navigateToSubmission(homework.id)
            StatusHw.Ongoing -> navigateToSubmission(homework.id)
            StatusHw.Upcoming -> navigateToSubmission(homework.id)
            else -> {

            }
        }
    }
    private fun navigateToCreation(hwId: String? = null, type: HomeworkType) {
        val lessonId = HomeworkFragmentArgs.fromBundle(requireArguments()).lessonId
        if (lessonId.isNullOrEmpty())
        safeNavigate(
            this,
            R.id.homeworkFragment,
            HomeworkFragmentDirections.actionHomeworkFragmentToHomeworkCreationFragment(
                classId = HomeworkFragmentArgs.fromBundle(requireArguments()).classId,
                hwId = hwId,
                hwType = type.toString(),
                lessonId = lessonId,
            )
        )
        else
            safeNavigate(
                requireParentFragment(),
                R.id.topicFragment,
                TopicFragmentDirections.actionTopicFragmentToHomeworkCreation(
                    classId = HomeworkFragmentArgs.fromBundle(requireArguments()).classId,
                    hwId = hwId,
                    hwType = type.toString(),
                    lessonId = lessonId,
                )
            )
    }

    private fun navigateToDraftDashboard() {
        val lessonId = HomeworkFragmentArgs.fromBundle(requireArguments()).lessonId
        if (lessonId.isNullOrEmpty())
            safeNavigate(
                this,
                R.id.homeworkFragment,
                HomeworkFragmentDirections.actionHomeworkFragmentToHomeworkDraftFragment(
                    HomeworkFragmentArgs.fromBundle(requireArguments()).classId,
                )
            )
        else
            safeNavigate(
                this,
                R.id.topicFragment,
                TopicFragmentDirections.actionTopicFragmentToDrafts(
                    classId = HomeworkFragmentArgs.fromBundle(requireArguments()).classId,
                    lessonId = lessonId
                )
            )
    }

    private fun navigateToLinkToLesson(tfileId:String) = safeNavigate(
        this,R.id.homeworkFragment,
        HomeworkFragmentDirections.actionHomeworkFragmentToLessonSelectionFragment(tfileId)
    )


    private fun navigateToSubmission(hwId: String) {
        val lessonId = HomeworkFragmentArgs.fromBundle(requireArguments()).lessonId
        if (lessonId.isNullOrEmpty()) {
            safeNavigate(
                this,
                R.id.homeworkFragment,
                HomeworkFragmentDirections.actionHomeworkFragmentToHomeworkSummaryFragment(
                    hwId = hwId,
                    classId = HomeworkFragmentArgs.fromBundle(requireArguments()).classId,
                    lessonId = lessonId,
                )
            )
        }
        else {
            safeNavigate(
                requireParentFragment(),
                R.id.topicFragment,
                TopicFragmentDirections.actionTopicFragmentToHomeworkSummary(
                    classId = HomeworkFragmentArgs.fromBundle(requireArguments()).classId,
                    hwId = hwId,
                    lessonId = lessonId,
                )
            )
        }
    }
}