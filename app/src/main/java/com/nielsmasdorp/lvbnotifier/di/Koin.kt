package com.nielsmasdorp.lvbnotifier.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import com.babylon.certificatetransparency.certificateTransparencyInterceptor
import com.nielsmasdorp.domain.auth.TokenManager
import com.nielsmasdorp.domain.buybox.BuyBoxRepository
import com.nielsmasdorp.domain.clipboard.Clipboard
import com.nielsmasdorp.domain.clipboard.SaveProductEAN
import com.nielsmasdorp.domain.notifications.MessageProvider
import com.nielsmasdorp.domain.notifications.NotificationHandler
import com.nielsmasdorp.domain.notifications.SendNotification
import com.nielsmasdorp.domain.settings.*
import com.nielsmasdorp.domain.stock.*
import com.nielsmasdorp.domain.stock.CheckNewLVBState
import com.nielsmasdorp.lvbnotifier.data.auth.AuthService
import com.nielsmasdorp.lvbnotifier.data.auth.OAuth2Authenticator
import com.nielsmasdorp.lvbnotifier.data.auth.OAuth2Interceptor
import com.nielsmasdorp.lvbnotifier.data.auth.OAuth2TokenManager
import com.nielsmasdorp.lvbnotifier.data.buybox.ScrapeBuyBoxRepository
import com.nielsmasdorp.lvbnotifier.data.buybox.network.BuyBoxService
import com.nielsmasdorp.lvbnotifier.data.clipboard.AndroidClipboard
import com.nielsmasdorp.lvbnotifier.data.notifications.AndroidMessageProvider
import com.nielsmasdorp.lvbnotifier.data.notifications.telegram.TelegramNotificationHandler
import com.nielsmasdorp.lvbnotifier.data.notifications.telegram.network.TelegramService
import com.nielsmasdorp.lvbnotifier.data.network.CacheInterceptor
import com.nielsmasdorp.lvbnotifier.data.notifications.system.SystemNotificationHandler
import com.nielsmasdorp.lvbnotifier.data.settings.SharedPreferencesSettingsRepository
import com.nielsmasdorp.lvbnotifier.data.stock.ApiStockRepository
import com.nielsmasdorp.lvbnotifier.data.stock.SharedPreferencesLastKnownStockRepository
import com.nielsmasdorp.lvbnotifier.data.stock.network.StockService
import com.nielsmasdorp.lvbnotifier.presentation.settings.SettingsViewModel
import com.nielsmasdorp.lvbnotifier.presentation.stock.StockViewModel
import com.nielsmasdorp.lvbnotifier.util.EncryptedSharedPreferenceDataStore
import com.nielsmasdorp.lvbnotifier.util.SharedPrefsDataStore
import com.nielsmasdorp.lvbnotifier.work.StateUpdateScheduler
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File

private fun createLVBStockService(client: OkHttpClient): StockService {
    return Retrofit.Builder()
        .baseUrl("https://api.bol.com/retailer/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(StockService::class.java)
}

private fun createMessageService(client: OkHttpClient): TelegramService {
    return Retrofit.Builder()
        .baseUrl("https://api.telegram.org")
        .client(client)
        .build()
        .create(TelegramService::class.java)
}

private fun createBuyBoxService(client: OkHttpClient): BuyBoxService {
    return Retrofit.Builder()
        .baseUrl("https://www.bol.com/nl/rnwy/artikelen/")
        .client(client)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(BuyBoxService::class.java)
}

private fun getCacheDir(context: Context): File {
    return File(context.applicationContext.cacheDir, "http-cache")
}

val settingsModule = module {
    single<SharedPrefsDataStore> { EncryptedSharedPreferenceDataStore(androidApplication()) }
    single<SettingsRepository> {
        SharedPreferencesSettingsRepository(
            get()
        )
    }
}

val notificationsModule = module {
    single { NotificationManagerCompat.from(androidApplication()) }
    single<NotificationHandler>("telegramNotificationHandler", definition = {
        TelegramNotificationHandler(
            createMessageService(get("defaultClient")),
            get()
        )
    })
    single<NotificationHandler>("systemNotificationHandler", definition = {
        SystemNotificationHandler(get(), get())
    })
    single {
        SendNotification(
            get("telegramNotificationHandler"),
            get("systemNotificationHandler"),
            get()
        )
    }
    single { WorkManager.getInstance(androidApplication()) }
    single {
        StateUpdateScheduler(
            get(),
            get(),
            get()
        )
    }
    single { ShouldPoll(get(), get()) }
    single { CanSendNotifications(get()) }
    single { CanSendBuyBoxNotifications(get(), get()) }
    single { CheckNewLVBState(get(), get(), get(), get(), get(), get(), get(), get()) }
}

val stockModule = module {
    single<LastKnownStockRepository> {
        SharedPreferencesLastKnownStockRepository(
            get()
        )
    }
    single<StockRepository> {
        ApiStockRepository(
            createLVBStockService(get("stockClient")),
            get()
        )
    }
    single { GetLastKnownStock(get()) }
    single { SetLastKnownStock(get()) }
    single { GetStock(get()) }
    single { CanShowStock(get()) }
}

val buyBoxModule = module {
    single<BuyBoxRepository> {
        ScrapeBuyBoxRepository(
            createBuyBoxService(get("buyBoxClient")),
            get()
        )
    }
    single { GetBuyBoxStatus(get()) }
    single { SetBuyBoxStatus(get()) }
}

val clipBoardModule = module {
    single { SaveProductEAN(get()) }
    single<Clipboard> { AndroidClipboard(androidApplication()) }
}

val authModule = module {
    single<AuthService> {
        Retrofit.Builder()
            .baseUrl("https://login.bol.com/")
            .client(get("defaultClient"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
    single<TokenManager> {
        OAuth2TokenManager(
            context = androidApplication(),
            authService = get(),
            settingsRepository = get()
        )
    }
}

val networkModule = module {
    single("certTransparencyInterceptor", definition = {
        certificateTransparencyInterceptor {
            // Enable for all hosts
            +"*.*"
        }
    })
    single("defaultClient", definition = {
        OkHttpClient.Builder().apply {
            addNetworkInterceptor(get("certTransparencyInterceptor"))
            //addInterceptor(ChuckInterceptor(androidApplication()))
        }.build()
    })
    single("buyBoxClient", definition = {
        OkHttpClient.Builder().apply {
            addNetworkInterceptor(get("certTransparencyInterceptor"))
            addNetworkInterceptor(CacheInterceptor())
            cache(get())
            //addInterceptor(ChuckInterceptor(androidApplication()))
        }.build()
    })
    single("stockClient", definition = {
        OkHttpClient.Builder().apply {
            addNetworkInterceptor(get("certTransparencyInterceptor"))
            addNetworkInterceptor(CacheInterceptor())
            addInterceptor(OAuth2Interceptor(get()))
            authenticator(OAuth2Authenticator(get()))
            cache(get())
            //addInterceptor(ChuckInterceptor(androidApplication()))
        }.build()
    })
    single {
        Cache(getCacheDir(get()), 10 * 1024 * 1024.toLong()) // 10 MiB
    }
}

val uiModule = module {
    single<MessageProvider> { AndroidMessageProvider(androidApplication()) }
    viewModel { StockViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get()) }
}