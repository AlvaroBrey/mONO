package com.ontherunvaro.onoclient.util

/**
 * Created by varo on 5/11/16.
 */

object OnoURL {

    val BASE_URL = "ono.es"
    private val SEPARATOR = "/"

    fun builder(): OnoURLBuilder {
        return OnoURLBuilder()
    }

    enum class OnoPage(internal val value: String) {

        CLIENT_AREA("clientes"), LOGIN("area-cliente/login");

        override fun toString(): String {
            return value
        }
    }

    class OnoURLBuilder internal constructor() {

        private val current: StringBuilder = StringBuilder("https://" + BASE_URL)

        fun withPage(page: OnoPage): OnoURLBuilder {
            current.append(SEPARATOR)
            current.append(page.value)
            return this
        }

        fun withString(value: String): OnoURLBuilder {
            current.append(SEPARATOR)
            current.append(value)
            return this
        }

        override fun toString(): String {
            return current.toString()
        }
    }
}
