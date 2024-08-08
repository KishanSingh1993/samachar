package com.kishan.samachar.data.remote.model

import com.kishan.samachar.data.model.Country
import com.kishan.samachar.utils.AppConstants.DEFAULT_COUNTRY_CODE
import com.kishan.samachar.utils.AppConstants.DEFAULT_COUNTRY_FLAG
import com.kishan.samachar.utils.AppConstants.DEFAULT_COUNTRY_NAME
import com.kishan.samachar.utils.AppConstants.DEFAULT_LANGUAGE_CODE
import com.kishan.samachar.utils.AppConstants.DEFAULT_SOURCE

data class HeadlinesParams(
    val selectedCountry: Country = Country(
        code = DEFAULT_COUNTRY_CODE,
        name = DEFAULT_COUNTRY_NAME,
        flag = DEFAULT_COUNTRY_FLAG
    ),
    val selectedLanguageCode: String = DEFAULT_LANGUAGE_CODE,
    val selectedSourceId: String = DEFAULT_SOURCE
)