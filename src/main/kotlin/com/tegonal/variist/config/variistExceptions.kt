package com.tegonal.variist.config

abstract class VariistConfigException(message: String, cause: Throwable?) : RuntimeException(message, cause)
class VariistParseException(message: String, cause: Throwable? = null) : VariistConfigException(message, cause)
class VariistDeadlineException(message: String) : VariistConfigException(message, null)
