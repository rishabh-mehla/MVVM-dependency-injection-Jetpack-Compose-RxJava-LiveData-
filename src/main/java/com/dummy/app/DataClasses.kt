package com.dummy.app

import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.Keep
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class Cryptocurrency (
    val image:  String,
    val name: String
)

sealed interface RandonUIEvent{
    class RandomUI(val ss: Boolean): RandonUIEvent
}


//@JsonClass(generateAdapter = true)
//data class Obj(
//    @Json(name = "uid") val uid: String,
//    @Json(name = "msg") val msg: String? = null
//)
//
//class ApiResponse(status: Boolean, msg: String?, obj: Obj?) : BaseResponse<Obj>(status, msg, obj)
//
//class RequestParams(val id: String)
//
//data class DeleteParams(var phone_number: String? = null, var email: String? = null)
//
//@JsonClass(generateAdapter = true)
//data class DeleteUser(
//    @Json(name = "deleted") val deleted: Boolean? = false
//)
//
//class DeleteResponse(status: Boolean, msg: String?, obj: DeleteUser?) :
//    BaseResponse<DeleteUser>(status, msg, obj)
//
//@JsonClass(generateAdapter = true)
//data class InstituteUser(
//    @Json(name = "msg") val msg: String? = null,
//    @Json(name = "institute_member") val instituteMember: Boolean? = false
//)
//
//class CheckUserResponse(status: Boolean, msg: String?, obj: InstituteUser?) :
//    BaseResponse<InstituteUser>(status, msg, obj)
//
//
//class CountriesResponse(status: Boolean, msg: String?, obj: List<CountryItem>?) :
//    BaseResponse<List<CountryItem>>(status, msg, obj)
//
//@Parcelize
//data class CountryItem(
//    val _id: String,
//    val c: Double? = 0.0,
//    val u: Double? = 0.0,
//    val deleted: Boolean? = false,
//    val country: String,
//    val isd_code: String,
//    val min_length: Int?,
//    val max_length: Int?,
//    val is_active: Boolean? = true,
//    val iso_country_code: String,
//    val flag_url: String?,
//    val tnc_url: String?,
//    val privacy_url: String?,
//    val timezone: String? = null,
//    val currency: String? = null
//) : Parcelable {
//
//    fun getDisplayMobileCountryCode() = "+$isd_code"
//
//    companion object {
//        val IndiaConfig = CountryItem(
//            _id = "616d0c549a05e07735dd1de5",
//            country = "India",
//            isd_code = "91",
//            min_length = 10,
//            max_length = 11,
//            iso_country_code = "in",
//            flag_url = "https://storage.googleapis.com/teachmint/tm_flags/India.webp",
//            tnc_url = "https://www.teachmint.com/terms",
//            privacy_url = "https://www.teachmint.com/privacy"
//        )
//    }
//}
//
//
///**
// * Created by Tushar Garg on 25/08/22.
// */
//@Keep
//@Serializable
//data class KtorBaseResponse<T : Any?>(
//    val obj: T? = null,
//    val msg: String? = null,
//    val status: Boolean,
//    @SerialName("error_code")val errorCode:Int? = null
//
//)
//
//@Keep
//@Serializable
//data class DefaultResponse(
//    val error: String = "",
//    val message: String = "",
//    val status: Boolean
//)
//
//
//fun <T : Any?> KtorBaseResponse<T>.unWrap(status: HttpStatusCode): T {
//    val baseResponse = this
//    return if (status.isSuccess() && baseResponse.obj != null) {
//        baseResponse.obj
//    } else {
//        throw BaseResponseException(baseResponse)
//    }
//}
//
//suspend inline fun <reified T : Any?, reified R : KtorBaseResponse<T>> apiCall(crossinline request: suspend () -> HttpResponse): T {
//
//    try {
//        val response = request()
//        val baseResponse = response.body<R>()
//        if(response.status.isSuccess()) {
//            if(baseResponse.status && baseResponse.obj != null) {
//                return response.body<R>().unWrap(response.status)
//            } else {
//                throw mapServerException(baseResponse.errorCode, baseResponse.msg)
//            }
//        }
//        throw response.body<Throwable>().cause?: KtorExceptions.UnexpectedException("Something went wrong")
//    } catch (exception: Exception) {
//        throw KtorExceptions.UnexpectedException(exception.message?: "Something went wrong")
//    }
//}
//
//suspend inline fun <reified T> HttpClient.getWithBody(
//    urlString: String,
//    body: T,
//): HttpResponse = get {
//    url(urlString)
//    setBody(body.toString())
//}
//
///**
// * Executes a [HttpClient] POST request, with the specified [url] as URL and
// * specified [body] as request body
// */
//suspend inline fun <reified T> HttpClient.post(
//    urlString: String,
//    body: T,
//): HttpResponse = post { url(urlString); setBody(body) }
//
//@Suppress("MemberVisibilityCanBePrivate")
//class BaseResponseException(
//    val baseResponse: KtorBaseResponse<out Any?>,
//) : DisplayException(baseResponse.msg ?: "", Exception("Failed server response: $baseResponse"))
//open class DisplayException(
//    message: String,
//    cause: Throwable = Exception(),
//) : Exception(message, cause)
//
//fun Exception.withDefaultMessage(message: String): DisplayException {
//    if (this is DisplayException) {
//        return this
//    }
//    return DisplayException(message = message, cause = this)
//}
//
//interface TaskRequest<T> {
//    fun refresh()
//}
//
//interface TaskResponse<T> {
//    val isRunningFlow: StateFlow<Boolean>
//    val taskStateFlow: Flow<TaskState<T>>
//}
//
//open class Task<T>(
//    start: TaskStart = TaskStart.Eagerly,
//    private val showErrorToast: Boolean = true,
//    private val block: suspend () -> T,
//) : TaskRequest<T>, TaskResponse<T> {
//
//    private val refreshChannel = MutableSharedFlow<Unit>(
//        replay = 1,
//        onBufferOverflow = BufferOverflow.DROP_LATEST
//    ).apply {
//        if (start == TaskStart.Eagerly) {
//            tryEmit(Unit)
//        }
//    }
//
//    private val _isRunningFlow = MutableStateFlow(false)
//    override val isRunningFlow = _isRunningFlow.asStateFlow()
//
//    private var hasLoadedData = false
//
//    override val taskStateFlow: Flow<TaskState<T>> = refreshChannel.transform {
//        _isRunningFlow.value = true
//        try {
//            if (!hasLoadedData) {
//                emit(TaskState.Loading)
//            }
//            emit(TaskState.Success(block()))
//            hasLoadedData = true
//        } catch (e: Exception) {
//            if (!hasLoadedData) {
//                emit(TaskState.Error(e))
//            }
//            if (e is DisplayException && showErrorToast) {
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//                }
//            }
//        } finally {
//            _isRunningFlow.value = false
//        }
//    }.flowOn(Dispatchers.Default)
//
//    override fun refresh() {
//        refreshChannel.tryEmit(Unit)
//    }
//}
//
//open class TaskFlow<T>(
//    start: TaskStart = TaskStart.Eagerly,
//    private val block: suspend () -> Flow<T>,
//) : TaskRequest<T>, TaskResponse<T> {
//
//    private val refreshChannel = MutableSharedFlow<Unit>(
//        replay = 1,
//        onBufferOverflow = BufferOverflow.DROP_LATEST
//    ).apply {
//        if (start == TaskStart.Eagerly) {
//            tryEmit(Unit)
//        }
//    }
//
//    private val _isRunningFlow = MutableStateFlow(false)
//    override val isRunningFlow = _isRunningFlow.asStateFlow()
//
//    override val taskStateFlow: Flow<TaskState<T>> = refreshChannel.transform {
//        _isRunningFlow.value = true
//        try {
//            emit(TaskState.Loading)
//            block().catch { e ->
//                emit(TaskState.Error(e))
//            }.collect {
//                emit(TaskState.Success(it))
//            }
//        } catch (e: Exception) {
//            emit(TaskState.Error(e))
//
//        } finally {
//            _isRunningFlow.value = false
//        }
//    }.catch{ e ->
//    }.flowOn(Dispatchers.Default)
//
//    override fun refresh() {
//        refreshChannel.tryEmit(Unit)
//    }
//}
//
//enum class TaskStart {
//    Lazy, Eagerly
//}
//
//fun <T> Task<T>.asTaskResponse(): TaskResponse<T> = this
//
//
//sealed class TaskState<out V> {
//    object Idle : TaskState<Nothing>()
//    object Loading : TaskState<Nothing>()
//    data class Success<out T>(val data: T) : TaskState<T>()
//    data class Error(val error: Throwable) : TaskState<Nothing>()
//}
//
//fun <V> TaskState<V>.getDataOrNull(): V? {
//    return if (this is TaskState.Success) {
//        data
//    } else {
//        null
//    }
//}
//
//inline fun <T : Any> taskStateFlow(
//    start: TaskStart = TaskStart.Eagerly,
//    crossinline block: suspend () -> T,
//): Flow<TaskState<T>> {
//    return Task(start = start, block = { block() }).taskStateFlow
//}
//
//fun <T : Any> Flow<TaskState<T>>.stateIn(
//    scope: CoroutineScope,
//    started: SharingStarted = SharingStarted.Eagerly,
//) = onEach {
//    if (it is TaskState.Error) {
//        val throwable = it.error
//        if (throwable is DisplayException) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//        }
//    }
//}.filterNot { (it is TaskState.Error && it.error is DisplayException) }.stateIn(scope, started, TaskState.Loading)
//
//fun <T : Any> Flow<TaskState<T>>.stateInWithDisplayException(
//    scope: CoroutineScope,
//    started: SharingStarted = SharingStarted.Eagerly,
//) = stateIn(scope, started, TaskState.Loading)
//
//fun <T : Any> Flow<T>.asTaskState(): Flow<TaskState<T>> = map<T, TaskState<T>> {
//    TaskState.Success(it)
//}.onStart {
//    emit(TaskState.Loading)
//}.catch {
//    emit(TaskState.Error(it))
//}
//
//fun <T : Any> Flow<TaskState<T>>.stateInSilentError(
//    scope: CoroutineScope,
//    started: SharingStarted = SharingStarted.Eagerly,
//) = filterNot { (it is TaskState.Error && it.error is DisplayException) }.stateIn(scope, started, TaskState.Loading)
//
//inline fun <T, R> Flow<TaskState<T>>.mapOnSuccess(
//    crossinline transform: suspend (value: T) -> R,
//): Flow<TaskState<R>> = map {
//    when (it) {
//        is TaskState.Success -> {
//            TaskState.Success(transform(it.data))
//        }
//        is TaskState.Error -> {
//            it
//        }
//        is TaskState.Loading -> {
//            it
//        }
//        is TaskState.Idle -> {
//            it
//        }
//    }
//}
//
//fun <T : Any> Flow<TaskState<T>>.unWrapTaskState() = transform {
//    if (it is TaskState.Success) {
//        emit(it.data)
//    }
//}
//
//fun <T : Any> StateFlow<TaskState<T>>.onEachErrorShowToast(scope: CoroutineScope) = onEach {
//    if (it is TaskState.Error) {
//        val throwable = it.error
//        if (throwable is DisplayException) {
//            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
//        }
//    }
//}.stateIn(scope, SharingStarted.Eagerly, TaskState.Loading)
//
//@Composable
//inline fun <T> State<TaskState<T>>.Render(
//    crossinline onError: @Composable (Throwable) -> Unit = {},
//    crossinline onLoading: @Composable () -> Unit = {},
//    crossinline onSuccess: @Composable (T) -> Unit,
//) {
//    value.Render(
//        onError = onError,
//        onLoading = onLoading,
//        onSuccess = onSuccess
//    )
//}
//
//
//@Composable
//inline fun <T> TaskState<T>.Render(
//    crossinline onError: @Composable (Throwable) -> Unit = {},
//    crossinline onLoading: @Composable () -> Unit = {},
//    crossinline onSuccess: @Composable (T) -> Unit,
//) {
//    when (this) {
//        is TaskState.Success -> onSuccess(data)
//        is TaskState.Error -> onError(error)
//        is TaskState.Loading -> onLoading()
//        is TaskState.Idle -> {}
//    }
//}
//
//@Composable
//fun <T> rememberTaskStateSuccess(value: T): MutableState<TaskState<T>> =
//    rememberMutableState(TaskState.Success(value))
//
//@Composable
//fun <T> rememberMutableState(value: T): MutableState<T> =
//    remember { mutableStateOf(value) }