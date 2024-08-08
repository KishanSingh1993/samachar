package com.kishan.samachar.ui.base

import com.kishan.samachar.data.model.Country
import com.kishan.samachar.data.model.Language
import com.kishan.samachar.data.remote.model.Source

typealias ClickHandler = () -> Unit
typealias RetryHandler = () -> Unit
typealias DismissHandler = () -> Unit
typealias SearchHandler = (String) -> Unit
typealias UrlHandler = (String) -> Unit
typealias HeadlineHandler<T> = (headline: T) -> Unit
typealias SourceHandler = (Source) -> Unit
typealias LanguageHandler = (Language) -> Unit
typealias CountryHandler = (Country) -> Unit