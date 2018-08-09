/*
 * Copyright (c) 2018 DuckDuckGo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.duckduckgo.app.di

import android.content.Context
import com.duckduckgo.app.global.AppUrl
import com.duckduckgo.app.global.api.ApiRequestInterceptor
import com.duckduckgo.app.global.device.ContextDeviceInfo
import com.duckduckgo.app.global.device.DeviceInfo
import com.duckduckgo.app.statistics.VariantManager
import com.duckduckgo.app.statistics.api.PixelService
import com.duckduckgo.app.statistics.api.StatisticsRequester
import com.duckduckgo.app.statistics.api.StatisticsService
import com.duckduckgo.app.statistics.api.StatisticsUpdater
import com.duckduckgo.app.statistics.pixels.ApiBasedPixel
import com.duckduckgo.app.statistics.pixels.Pixel
import com.duckduckgo.app.statistics.store.StatisticsDataStore
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Named


@Module
class StatisticsModule {

    @Provides
    fun statisticsService(@Named("api") retrofit: Retrofit): StatisticsService = retrofit.create(StatisticsService::class.java)

    @Provides
    fun statisticsUpdater(
        statisticsDataStore: StatisticsDataStore,
        statisticsService: StatisticsService,
        variantManager: VariantManager
    ): StatisticsUpdater =
        StatisticsRequester(statisticsDataStore, statisticsService, variantManager)

    @Provides
    fun pixelService(@Named("pixel") okHttpClient: OkHttpClient, @Named("pixel") retrofit: Retrofit): PixelService {
        return retrofit.create(PixelService::class.java)
    }

    @Provides
    fun deviceInfo(context: Context): DeviceInfo = ContextDeviceInfo(context)

    @Provides
    fun pixel(pixelService: PixelService, statisticsDataStore: StatisticsDataStore, variantManager: VariantManager, deviceInfo: DeviceInfo): Pixel =
        ApiBasedPixel(pixelService, statisticsDataStore, variantManager, deviceInfo)

}