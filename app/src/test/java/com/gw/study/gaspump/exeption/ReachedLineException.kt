package com.gw.study.gaspump.exeption

class ReachedLineException(message: String? = "Line Reached") : Throwable(message) {
    constructor(throwable: Throwable) : this(throwable.message)

}